package mrriegel.storagenetwork.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mezz.jei.Internal;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.helper.Settings;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.network.ClearMessage;
import mrriegel.storagenetwork.network.InsertMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.RequestMessage;
import mrriegel.storagenetwork.network.SortMessage;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

public abstract class AbstractGuiRequest extends MyGuiContainer {
	protected ResourceLocation texture;
	protected int page = 1, maxPage = 1;
	public List<StackWrapper> stacks, craftableStacks;
	protected ItemStack over;
	protected GuiTextField searchBar;
	protected Button direction, sort, left, right, jei;
	protected List<ItemSlot> slots;
	protected long lastClick;

	public AbstractGuiRequest(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 256;
		this.stacks = Lists.newArrayList();
		this.craftableStacks = Lists.newArrayList();
		PacketHandler.INSTANCE.sendToServer(new RequestMessage(0, null, false, false));
		lastClick = System.currentTimeMillis();
	}

	protected boolean canClick() {
		return System.currentTimeMillis() > lastClick + 100L;
	}

	protected abstract int getLines();

	protected abstract int getColumns();

	protected abstract boolean getDownwards();

	protected abstract void setDownwards(boolean d);

	protected abstract Sort getSort();

	protected abstract void setSort(Sort s);

	protected abstract BlockPos getPos();

	// protected abstract BlockPos getMaster();

	protected abstract int getDim();

	protected abstract boolean inField(int mouseX, int mouseY);

	protected abstract boolean inSearchbar(int mouseX, int mouseY);

	protected abstract boolean inX(int mouseX, int mouseY);

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		String search = searchBar.getText();
		List<StackWrapper> tmp = search.equals("") ? Lists.newArrayList(stacks) : Lists.<StackWrapper> newArrayList();
		if (!search.equals("")) {
			for (StackWrapper s : stacks)
				if (search.startsWith("@")) {
					String name = Util.getModNameForItem(s.getStack().getItem());
					if (name.toLowerCase().contains(search.toLowerCase().substring(1)))
						tmp.add(s);
				} else if (search.startsWith("#")) {
					String tooltipString;
					List<String> tooltip = s.getStack().getTooltip(mc.thePlayer, false);
					tooltipString = Joiner.on(' ').join(tooltip).toLowerCase();
					tooltipString = ChatFormatting.stripFormatting(tooltipString);
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
		// for (StackWrapper s : craftableStacks)
		// tmp.add(s);
		Collections.sort(tmp, new Comparator<StackWrapper>() {
			int mul = getDownwards() ? -1 : 1;

			@Override
			public int compare(StackWrapper o2, StackWrapper o1) {
				switch (getSort()) {
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
		maxPage = tmp.size() / (getLines() * getColumns());
		if (tmp.size() % (getLines() * getColumns()) != 0)
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
		int index = (page - 1) * (getLines() * getColumns());
		for (int jj = 0; jj < getLines(); jj++) {
			for (int ii = 0; ii < getColumns(); ii++) {
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
			if (s.isMouseOverSlot(mouseX, mouseY)) {
				over = s.stack;
				break;
			} else
				over = null;
		}
		if (slots.isEmpty())
			over = null;

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for (ItemSlot s : slots) {
			s.drawTooltip(mouseX, mouseY);
		}
		// if (inX(mouseX, mouseY))
		// drawHoveringText(Lists.newArrayList("Clear the crafting grid."),
		// mouseX - guiLeft, mouseY - guiTop);
		if (inSearchbar(mouseX, mouseY)) {
			List<String> lis = Lists.newArrayList("Right click to clear the search bar.");
			if (!isShiftKeyDown())
				lis.add(ChatFormatting.ITALIC + "Hold shift for more information.");
			else {
				lis.add("Prefix @: Search for mod.");
				lis.add("Prefix #: Search for tooltip.");
				lis.add("Prefix $: Search for OreDict.");
				lis.add("Prefix %: Search for creative tab.");
			}
			drawHoveringText(lis, mouseX - guiLeft, mouseY - guiTop);
		}
		if (sort.isMouseOver())
			drawHoveringText(Lists.newArrayList(I18n.format("gui.storagenetwork.req.tooltip_" + getSort().toString())), mouseX - guiLeft, mouseY - guiTop);
		if (jei != null && jei.isMouseOver())
			drawHoveringText(Lists.newArrayList(Settings.jeiSearch ? "JEI search enabled" : "JEI search disabled"), mouseX - guiLeft, mouseY - guiTop);
		if (searchBar.isFocused() && ConfigHandler.jeiLoaded && Settings.jeiSearch) {
			Internal.getRuntime().getItemListOverlay().setFilterText(searchBar.getText());
		}
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
		else if (button.id == 3 && page < maxPage)
			page++;
		else if (button.id == 0)
			setDownwards(!getDownwards());
		else if (button.id == 1)
			setSort(getSort().next());
		else if (button.id == 4)
			Settings.jeiSearch = !Settings.jeiSearch;
		PacketHandler.INSTANCE.sendToServer(new SortMessage(getPos(), getDownwards(), getSort()));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchBar.setFocused(false);
		if (inSearchbar(mouseX, mouseY)) {
			if (mouseButton == 1)
				searchBar.setText("");
			searchBar.setFocused(true);
		} else if (inX(mouseX, mouseY)) {
			PacketHandler.INSTANCE.sendToServer(new ClearMessage());
			PacketHandler.INSTANCE.sendToServer(new RequestMessage(0, null, false, false));
		} else if (over != null && (mouseButton == 0 || mouseButton == 1) && mc.thePlayer.inventory.getItemStack() == null && canClick()) {
			PacketHandler.INSTANCE.sendToServer(new RequestMessage(mouseButton, over, isShiftKeyDown(), isCtrlKeyDown()));
			lastClick = System.currentTimeMillis();
		} else if (mc.thePlayer.inventory.getItemStack() != null && inField(mouseX, mouseY) && canClick()) {
			PacketHandler.INSTANCE.sendToServer(new InsertMessage(getDim(), mc.thePlayer.inventory.getItemStack()));
			lastClick = System.currentTimeMillis();
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
		if (inField(i, j)) {
			int mouse = Mouse.getEventDWheel();
			if (mouse == 0)
				return;
			if (mouse > 0 && page > 1)
				page--;
			if (mouse < 0 && page < maxPage)
				page++;
		}

	}

	public class Button extends GuiButton {

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
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 176 + (getDownwards() ? 6 : 0), 14, 6, 8);
				}
				if (id == 1) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 188 + (getSort() == Sort.AMOUNT ? 6 : getSort() == Sort.MOD ? 12 : 0), 14, 6, 8);
				}
				if (id == 4) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 176 + (Settings.jeiSearch ? 0 : 6), 22, 6, 8);
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
