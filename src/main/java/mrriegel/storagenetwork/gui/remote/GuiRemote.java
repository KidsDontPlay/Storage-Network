package mrriegel.storagenetwork.gui.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.network.InsertMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.RemoteMessage;
import mrriegel.storagenetwork.network.SortMessage;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.mojang.realmsclient.gui.ChatFormatting;

public class GuiRemote extends GuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/remote.png");
	int page = 1, maxPage = 1;
	public List<StackWrapper> stacks;
	ItemStack over;
	private GuiTextField searchBar;
	private Button direction, sort, left, right;

	public GuiRemote(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 256;
		stacks = new ArrayList<StackWrapper>();
		PacketHandler.INSTANCE.sendToServer(new RemoteMessage(0, NBTHelper.getInteger(Minecraft.getMinecraft().thePlayer.getHeldItem(), "x"), NBTHelper.getInteger(Minecraft.getMinecraft().thePlayer.getHeldItem(), "y"), NBTHelper.getInteger(Minecraft.getMinecraft().thePlayer.getHeldItem(), "z"), NBTHelper.getInteger(Minecraft.getMinecraft().thePlayer.getHeldItem(), "id"), null, false, false));
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 81, guiTop + 96 + 64, 85, fontRendererObj.FONT_HEIGHT);
		searchBar.setMaxStringLength(30);
		searchBar.setEnableBackgroundDrawing(false);
		searchBar.setVisible(true);
		searchBar.setTextColor(16777215);
		searchBar.setCanLoseFocus(false);
		searchBar.setFocused(true);
		direction = new Button(0, guiLeft + 7, guiTop + 93 + 64, "");
		buttonList.add(direction);
		sort = new Button(1, guiLeft + 21, guiTop + 93 + 64, "");
		buttonList.add(sort);
		left = new Button(2, guiLeft + 44, guiTop + 93 + 64, "<");
		buttonList.add(left);
		right = new Button(3, guiLeft + 58, guiTop + 93 + 64, ">");
		buttonList.add(right);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (mc.thePlayer.getHeldItem() == null) {
			mc.thePlayer.closeScreen();
			return;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
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
		Collections.sort(tmp, new Comparator<StackWrapper>() {
			int mul = NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "down") ? -1 : 1;

			@Override
			public int compare(StackWrapper o2, StackWrapper o1) {
				switch (Sort.valueOf(NBTHelper.getString(mc.thePlayer.getHeldItem(), "sort"))) {
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
		maxPage = tmp.size() / 56;
		if (tmp.size() % 56 != 0)
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
		int index = (page - 1) * 56;
		for (int jj = 0; jj < 7; jj++) {
			for (int ii = 0; ii < 8; ii++) {
				int in = index;
				if (in >= tmp.size())
					break;
				new Slot(tmp.get(in).getStack(), guiLeft + 10 + ii * 20, guiTop + 10 + jj * 20, tmp.get(in).getSize(), guiLeft, guiTop).drawSlot(mouseX, mouseY);
				index++;
			}
		}
		index = (page - 1) * 56;
		for (int jj = 0; jj < 7; jj++) {
			for (int ii = 0; ii < 8; ii++) {
				int in = index;
				if (in >= tmp.size())
					break;
				new Slot(tmp.get(in).getStack(), guiLeft + 10 + ii * 20, guiTop + 10 + jj * 20, tmp.get(in).getSize(), guiLeft, guiTop).drawTooltip(mouseX, mouseY);
				index++;
			}
		}
		searchBar.drawTextBox();

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
			NBTHelper.setBoolean(mc.thePlayer.getHeldItem(), "down", !NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "down"));
		else if (button.id == 1)
			NBTHelper.setString(mc.thePlayer.getHeldItem(), "sort", Sort.valueOf(NBTHelper.getString(mc.thePlayer.getHeldItem(), "sort")).next().toString());
		PacketHandler.INSTANCE.sendToServer(new SortMessage(NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "x"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "y"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "z"), NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "down"), Sort.valueOf(NBTHelper.getString(mc.thePlayer.getHeldItem(), "sort"))));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (over != null && mc.thePlayer.inventory.getItemStack() == null && (mouseButton == 0 || mouseButton == 1)) {
			PacketHandler.INSTANCE.sendToServer(new RemoteMessage(mouseButton, NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "x"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "y"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "z"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "id"), over, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT), Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)));
		}
		int i = Mouse.getX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (i > (guiLeft + 81) && i < (guiLeft + xSize - 7) && j > (guiTop + 96 + 64) && j < (guiTop + 103 + 64) && mouseButton == 1) {
			searchBar.setText("");
		}
		if (mc.thePlayer.inventory.getItemStack() != null && i > (guiLeft + 7) && i < (guiLeft + xSize - 7) && j > (guiTop + 7) && j < (guiTop + 90 + 64)) {
			PacketHandler.INSTANCE.sendToServer(new InsertMessage(NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "x"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "y"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "z"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "id"), mc.thePlayer.inventory.getItemStack()));
		}
	}

	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException {
		if (!this.checkHotbarKeys(p_73869_2_)) {
			Keyboard.enableRepeatEvents(true);
			if (this.searchBar.textboxKeyTyped(p_73869_1_, p_73869_2_)) {
				PacketHandler.INSTANCE.sendToServer(new RemoteMessage(0, NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "x"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "y"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "z"), NBTHelper.getInteger(mc.thePlayer.getHeldItem(), "id"), null, false, false));
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
		if (i > (guiLeft + 7) && i < (guiLeft + xSize - 7) && j > (guiTop + 7) && j < (guiTop + 90 + 64)) {
			int mouse = Mouse.getEventDWheel();
			if (mouse == 0)
				return;
			if (mouse > 0 && page > 1)
				page--;
			if (mouse < 0 && page < maxPage)
				page++;
		}

	}

	class Slot {
		ItemStack stack;
		int x, y, size, guiLeft, guiTop;

		public Slot(ItemStack stack, int x, int y, int size, int guiLeft, int guiTop) {
			this.stack = stack;
			this.x = x;
			this.y = y;
			this.size = size;
			this.guiLeft = guiLeft;
			this.guiTop = guiTop;
		}

		Minecraft mc = Minecraft.getMinecraft();

		void drawSlot(int mx, int my) {
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
			String amount = size < 1000 ? String.valueOf(size) : size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
			if (ConfigHandler.smallFont) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(.5f, .5f, .5f);
				mc.getRenderItem().renderItemOverlayIntoGUI(fontRendererObj, stack, x * 2 + 16, y * 2 + 16, amount);
				GlStateManager.popMatrix();
			} else
				mc.getRenderItem().renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, amount);
			if (this.isMouseOverSlot(mx, my)) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = x;
				int k1 = y;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				over = stack;
			}
		}

		void drawTooltip(int mx, int my) {
			if (this.isMouseOverSlot(mx, my)) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
					renderToolTip(stack, mx, my);
				else
					drawHoveringText(Arrays.asList(new String[] { "Amount: " + String.valueOf(size) }), mx, my);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		private boolean isMouseOverSlot(int mouseX, int mouseY) {
			return isPointInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
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
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 176 + (NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "down") ? 6 : 0), 14, 6, 8);
				}
				if (id == 1) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 188 + (Sort.valueOf(NBTHelper.getString(mc.thePlayer.getHeldItem(), "sort")) == Sort.AMOUNT ? 6 : Sort.valueOf(NBTHelper.getString(mc.thePlayer.getHeldItem(), "sort")) == Sort.MOD ? 12 : 0), 14, 6, 8);

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
