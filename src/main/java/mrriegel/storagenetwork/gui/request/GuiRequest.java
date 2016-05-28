package mrriegel.storagenetwork.gui.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.gui.MyGuiContainer;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.network.ClearMessage;
import mrriegel.storagenetwork.network.InsertMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.RequestMessage;
import mrriegel.storagenetwork.network.SortMessage;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

public class GuiRequest extends MyGuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/request.png");
	int page = 1, maxPage = 1;
	public List<StackWrapper> stacks, craftableStacks;
	ItemStack over;
	private GuiTextField searchBar;
	private Button direction, sort, left, right;
	TileRequest tile;
	private List<ItemSlot> slots;

	public GuiRequest(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 256;
		this.stacks = new ArrayList<StackWrapper>();
		tile = ((ContainerRequest) inventorySlots).tile;
		PacketHandler.INSTANCE.sendToServer(new RequestMessage(0, null, false, false));
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 81, guiTop + 96, 85, fontRendererObj.FONT_HEIGHT);
		searchBar.setMaxStringLength(30);
		searchBar.setEnableBackgroundDrawing(false);
		searchBar.setVisible(true);
		searchBar.setTextColor(16777215);
		direction = new Button(0, guiLeft + 7, guiTop + 93, "");
		buttonList.add(direction);
		sort = new Button(1, guiLeft + 21, guiTop + 93, "");
		buttonList.add(sort);
		left = new Button(2, guiLeft + 44, guiTop + 93, "<");
		buttonList.add(left);
		right = new Button(3, guiLeft + 58, guiTop + 93, ">");
		buttonList.add(right);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (tile == null || tile.getMaster() == null || !(tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster))
			mc.thePlayer.closeScreen();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		over = null;
		String search = searchBar.getText();
		List<StackWrapper> tmp = search.equals("") ? new ArrayList<StackWrapper>(stacks) : new ArrayList<StackWrapper>();
		if (!search.equals("")) {
			for (StackWrapper s : stacks)
				if (search.startsWith("@")) {
					String name = Util.getModNameForItem(s.getStack().getItem());
					if (name.toLowerCase().contains(search.toLowerCase().substring(1)))
						tmp.add(s);
				} else if (search.startsWith("#")) {
					String tooltipString;
					try {
						List<String> tooltip = s.getStack().getTooltip(mc.thePlayer, false);
						tooltipString = Joiner.on(' ').join(tooltip).toLowerCase();
						tooltipString = ChatFormatting.stripFormatting(tooltipString);
						String modId = GameData.getItemRegistry().getNameForObject(s.getStack().getItem()).getResourceDomain();
						tooltipString = tooltipString.replace(modId, "");
						ModContainer mod = Loader.instance().getIndexedModList().get(modId);
						String modName = mod == null ? "Minecraft" : mod.getName();
						tooltipString = tooltipString.replace(modName, "");
						tooltipString = tooltipString.replace(s.getStack().getDisplayName(), "");
					} catch (RuntimeException ignored) {
						tooltipString = "";
					}
					if (tooltipString.toLowerCase().contains(search.toLowerCase().substring(1)))
						tmp.add(s);
				} else if (search.startsWith("$")) {
					StringBuilder oreDictStringBuilder = new StringBuilder();
					for (int oreId : OreDictionary.getOreIDs(s.getStack())) {
						String oreName = OreDictionary.getOreName(oreId);
						oreDictStringBuilder.append(oreName).append(' ');
					}
					if (oreDictStringBuilder.toString().toLowerCase().contains(search.toLowerCase().substring(1)))
						tmp.add(s);
				} else if (search.startsWith("%")) {
					StringBuilder creativeTabStringBuilder = new StringBuilder();
					for (CreativeTabs creativeTab : s.getStack().getItem().getCreativeTabs()) {
						if (creativeTab != null) {
							String creativeTabName = creativeTab.getTranslatedTabLabel();
							creativeTabStringBuilder.append(creativeTabName).append(' ');
						}
					}
					if (creativeTabStringBuilder.toString().toLowerCase().contains(search.toLowerCase().substring(1)))
						tmp.add(s);
				} else {
					if (s.getStack().getDisplayName().toLowerCase().contains(search.toLowerCase()))
						tmp.add(s);
				}
		}
		if (craftableStacks != null)
			for (StackWrapper s : craftableStacks)
				tmp.add(s);
		Collections.sort(tmp, new Comparator<StackWrapper>() {
			int mul = tile.downwards ? -1 : 1;

			@Override
			public int compare(StackWrapper o2, StackWrapper o1) {
				switch (tile.sort) {
				case AMOUNT:
					return Integer.compare(o1.getSize(), o2.getSize()) * mul;
				case NAME:
					return o2.getStack().getDisplayName().compareToIgnoreCase(o1.getStack().getDisplayName()) * mul;
				case MOD:
					return Util.getModNameForItem(o2.getStack().getItem()).compareToIgnoreCase(Util.getModNameForItem(o1.getStack().getItem())) * mul;
				}
				return 0;
			}
		});
		maxPage = tmp.size() / 32;
		if (tmp.size() % 32 != 0)
			maxPage++;
		if (maxPage < 1)
			maxPage = 1;
		if (page < 1)
			page = 1;
		if (page > maxPage)
			page = maxPage;
		if (page == 1) {
			left.visible = false;
			left.enabled = false;
		} else {
			left.visible = true;
			left.enabled = true;
		}
		if (page == maxPage) {
			right.visible = false;
			right.enabled = false;
		} else {
			right.visible = true;
			right.enabled = true;
		}
		searchBar.drawTextBox();
		slots = Lists.newArrayList();
		int index = (page - 1) * 32;
		for (int jj = 0; jj < 4; jj++) {
			for (int ii = 0; ii < 8; ii++) {
				int in = index;
				if (in >= tmp.size())
					break;
				slots.add(new ItemSlot(tmp.get(in).getStack(), guiLeft + 10 + ii * 20, guiTop + 10 + jj * 20, tmp.get(in).getSize(), guiLeft, guiTop, true, true, ConfigHandler.smallFont, true));
				index++;
			}
		}
		for (ItemSlot s : slots) {
			s.drawSlot(mouseX, mouseY);
		}
		for (ItemSlot s : slots) {
			s.drawTooltip(mouseX, mouseY);
		}
		for (ItemSlot s : slots) {
			if (s.isMouseOverSlot(mouseX, mouseY)) {
				over = s.stack;
				break;
			}
		}

	}

	boolean mouseOverX(int mx, int my) {
		return isPointInRegion(63 - guiLeft, 110 - guiTop, 7, 7, mx, my);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 2 && page > 1)
			page--;
		if (button.id == 3 && page < maxPage)
			page++;
		if (button.id == 0)
			tile.downwards = !tile.downwards;
		else if (button.id == 1)
			tile.sort = tile.sort.next();
		PacketHandler.INSTANCE.sendToServer(new SortMessage(tile.getPos(), tile.downwards, tile.sort));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int i = Mouse.getX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		searchBar.setFocused(false);
		if (i > (guiLeft + 81) && i < (guiLeft + xSize - 7) && j > (guiTop + 96) && j < (guiTop + 103)) {
			if (mouseButton == 1)
				searchBar.setText("");
			searchBar.setFocused(true);
		} else if (mouseOverX(mouseX - guiLeft, mouseY - guiTop)) {
			PacketHandler.INSTANCE.sendToServer(new ClearMessage());
			PacketHandler.INSTANCE.sendToServer(new RequestMessage(0, null, false, false));
		} else if (over != null && (mouseButton == 0 || mouseButton == 1) && mc.thePlayer.inventory.getItemStack() == null) {
			PacketHandler.INSTANCE.sendToServer(new RequestMessage(mouseButton, over, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT), Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)));
		} else if (mc.thePlayer.inventory.getItemStack() != null && i > (guiLeft + 7) && i < (guiLeft + xSize - 7) && j > (guiTop + 7) && j < (guiTop + 90)) {
			TileEntity t = tile.getWorld().getTileEntity(tile.getMaster());
			PacketHandler.INSTANCE.sendToServer(new InsertMessage(t.getPos().getX(), t.getPos().getY(), t.getPos().getZ(), tile.getWorld().provider.getDimension(), mc.thePlayer.inventory.getItemStack()));
		}
	}

	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException {
		if (!this.checkHotbarKeys(p_73869_2_)) {
			Keyboard.enableRepeatEvents(true);
			if (this.searchBar.textboxKeyTyped(p_73869_1_, p_73869_2_)) {
				PacketHandler.INSTANCE.sendToServer(new RequestMessage(0, null, false, false));
			} else {
				super.keyTyped(p_73869_1_, p_73869_2_);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (i > (guiLeft + 7) && i < (guiLeft + xSize - 7) && j > (guiTop + 7) && j < (guiTop + 90)) {
			int mouse = Mouse.getEventDWheel();
			if (mouse == 0)
				return;
			if (mouse > 0 && page > 1)
				page--;
			if (mouse < 0 && page < maxPage)
				page++;
		}

	}

	class Button extends GuiButton {

		public Button(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, String p_i1021_6_) {
			super(p_i1021_1_, p_i1021_2_, p_i1021_3_, 14, 14, p_i1021_6_);
		}

		@Override
		public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
			if (this.visible) {
				FontRenderer fontrenderer = p_146112_1_.fontRendererObj;
				p_146112_1_.getTextureManager().bindTexture(texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
				int k = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 162 + 14 * k, 0, 14, 14);
				if (id == 0) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 176 + (tile.downwards ? 6 : 0), 14, 6, 8);
				}
				if (id == 1) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 188 + (tile.sort == Sort.AMOUNT ? 6 : tile.sort == Sort.MOD ? 12 : 0), 14, 6, 8);

				}
				this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
				int l = 14737632;

				if (packedFGColour != 0) {
					l = packedFGColour;
				} else if (!this.enabled) {
					l = 10526880;
				} else if (this.hovered) {
					l = 16777120;
				}

				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}
	}
}
