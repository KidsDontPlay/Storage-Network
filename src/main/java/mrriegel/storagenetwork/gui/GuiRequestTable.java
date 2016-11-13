package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mezz.jei.Internal;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.gui.Focus;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.GuiDrawer.Direction;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.StackWrapper;
import mrriegel.limelib.util.Utils;
import mrriegel.storagenetwork.container.ContainerRequestTable;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

public class GuiRequestTable extends CommonGuiContainer {

	TileRequestTable tile;
	public List<StackWrapper> wrappers;
	protected List<ItemSlot> items = Lists.newArrayList();
	protected long lastClick;
	protected GuiButtonSimple sort, direction, clear, jei;
	protected GuiTextField searchBar;
	protected int currentPos = 0, maxPos = 0;
	protected ItemStack over;

	protected boolean canClick() {
		return System.currentTimeMillis() > lastClick + 400L;
	}

	public GuiRequestTable(ContainerRequestTable inventorySlotsIn) {
		super(inventorySlotsIn);
		tile = inventorySlotsIn.tile;
		ySize = 220;
		xSize += 36 + 36;
		lastClick = System.currentTimeMillis();
		sendRequest(null, 0);
	}

	@Override
	public void initGui() {
		super.initGui();
		searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 154, guiTop + 121, 85, fontRendererObj.FONT_HEIGHT);
		searchBar.setMaxStringLength(30);
		searchBar.setEnableBackgroundDrawing(!false);
		searchBar.setVisible(true);
		searchBar.setTextColor(16777215);
		searchBar.setFocused(true);
		buttonList.add(sort = new GuiButtonSimple(0, guiLeft + 20, 126, 24, 12, "sort", null));
		buttonList.add(direction = new GuiButtonSimple(1, guiLeft + 50, 126, 24, 12, "direct", null));
		buttonList.add(clear = new GuiButtonSimple(2, guiLeft + 62, guiTop + 137, 11, 11, "x", Lists.newArrayList("Clear grid")));
		if (Loader.isModLoaded("JEI"))
			buttonList.add(jei = new GuiButtonSimple(3, guiLeft + 80, guiTop + 126, 24, 12, "", null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(79, 137);
		drawer.drawSlots(7, 7, 13, 6);
		//		drawer.drawTextfield(searchBar);
		searchBar.drawTextBox();
		drawer.drawSlots(7, 137, 3, 3);
		drawer.drawProgressArrow(13, 196, 0F, Direction.RIGHT);
		drawer.drawSlot(43, 195);
		drawer.drawColoredRectangle(7, 7, 13 * 18, 6 * 18, ColorHelper.getRGB(Color.DARK_GRAY.getRGB(), 80));
		over = null;
		if (wrappers != null) {
			int invisible = wrappers.size() - 13 * 6;
			if (invisible <= 0)
				maxPos = 0;
			else {
				maxPos = invisible / 13;
				if (invisible % 13 != 0)
					maxPos++;
			}
			if (currentPos > maxPos)
				currentPos = maxPos;
			int percent = (int) ((currentPos / (double) maxPos) * 100);
			drawer.drawColoredRectangle(3, 7 + percent, 4, 8, ColorHelper.darker(Color.DARK_GRAY.getRGB(), 0.1));
			items.clear();
			List<StackWrapper> tmp = getFilteredList();
			int index = currentPos * 13;
			line: for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 13; j++) {
					if (index >= tmp.size())
						break line;
					StackWrapper w = tmp.get(index);
					items.add(new ItemSlot(w.getStack(), index, guiLeft + 8 + j * 18, guiTop + 8 + i * 18, w.getSize(), drawer, true, true, true, true));
					index++;
				}
			}

			for (ItemSlot slot : items) {
				slot.draw(mouseX, mouseY);
				if (slot.isMouseOver(mouseX, mouseY)) {
					over = slot.stack;
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for (ItemSlot slot : items) {
			if (slot.isMouseOver(mouseX, mouseY))
				slot.drawTooltip(mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (searchBar.isFocused() && Loader.isModLoaded("JEI") && Internal.getRuntime().getItemListOverlay().hasKeyboardFocus()) {
			searchBar.setFocused(false);
		}
		sort.setTooltip("Sort by " + tile.sort.name().toLowerCase());
		sort.displayString = tile.sort.name().substring(0, 2);
		direction.setTooltip("Sort direction: " + (tile.topDown ? "top-down" : "bottom-up"));
		direction.displayString = tile.topDown ? "TD" : "BU";
		if (jei != null)
			jei.displayString = (tile.jei ? TextFormatting.GREEN : TextFormatting.RED) + "JEI";

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("button", button.id);
		tile.sendMessage(nbt);
		tile.handleMessage(mc.thePlayer, nbt);
	}

	protected void sendRequest(ItemStack stack, int mouseButton) {
		NBTTagCompound nbt = new NBTTagCompound();
		if (stack != null)
			stack.writeToNBT(nbt);
		nbt.setInteger("button", 1000);
		nbt.setInteger("mouse", mouseButton);
		nbt.setBoolean("shift", isShiftKeyDown());
		nbt.setBoolean("ctrl", isCtrlKeyDown());
		tile.sendMessage(nbt);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		searchBar.setFocused(isPointInRegion(searchBar.xPosition, searchBar.yPosition, searchBar.width, searchBar.height, mouseX + guiLeft, mouseY + guiTop));
		if (searchBar.isFocused() && mouseButton == 1) {
			searchBar.setText("");
			if (tile.jei && Loader.isModLoaded("JEI"))
				Internal.getRuntime().getItemListOverlay().setFilterText(searchBar.getText());
		}
		if (over != null && mc.thePlayer.inventory.getItemStack() == null)
			sendRequest(over, mouseButton);
		else if (mc.thePlayer.inventory.getItemStack() != null&&isPointInRegion(7, 7, 18 * 13, 18 * 6, GuiDrawer.getMouseX(), GuiDrawer.getMouseY())) {
			NBTTagCompound nbt = new NBTTagCompound();
			mc.thePlayer.inventory.getItemStack().writeToNBT(nbt);
			nbt.setInteger("button", 1001);
			nbt.setInteger("mouse", mouseButton);
			tile.sendMessage(nbt);
		}
		lastClick = System.currentTimeMillis();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		if (isPointInRegion(7, 7, 18 * 13, 18 * 6, GuiDrawer.getMouseX(), GuiDrawer.getMouseY())) {
			int mouse = Mouse.getEventDWheel();
			if (mouse == 0)
				return;
			if (mouse > 0 && currentPos > 0)
				currentPos--;
			if (mouse < 0 && currentPos < maxPos)
				currentPos++;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!this.checkHotbarKeys(keyCode)) {
			if (over != null && Loader.isModLoaded("JEI") && (keyCode == Keyboard.KEY_R || keyCode == Keyboard.KEY_U)) {
				if (keyCode == Keyboard.KEY_R)
					Internal.getRuntime().getRecipesGui().show(new Focus<ItemStack>(Mode.OUTPUT, over));
				else
					Internal.getRuntime().getRecipesGui().show(new Focus<ItemStack>(Mode.INPUT, over));
			} else

			if (this.searchBar.textboxKeyTyped(typedChar, keyCode)) {
				System.out.println(searchBar.getText());
				if (tile.jei && Loader.isModLoaded("JEI"))
					Internal.getRuntime().getItemListOverlay().setFilterText(searchBar.getText());
				sendRequest(null, 0);
			}
		}
		super.keyTyped(typedChar, keyCode);
	}

	private List<StackWrapper> getFilteredList() {
		String search = searchBar.getText().toLowerCase();
		List<StackWrapper> tmp = !search.isEmpty() ? Lists.<StackWrapper> newArrayList() : Lists.newArrayList(wrappers);
		if (!search.isEmpty())
			for (StackWrapper w : wrappers) {
				if (search.startsWith("@")) {
					String modID = Utils.getModID(w.getStack().getItem());
					if (modID.toLowerCase().contains(search.substring(1)))
						tmp.add(w);
				} else if (search.startsWith("#")) {
					List<String> tooltip = w.getStack().getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
					for (String s : tooltip) {
						s.replaceAll(w.getStack().getDisplayName(), " ");
						if (TextFormatting.getTextWithoutFormattingCodes(s).toLowerCase().contains(search.substring(1))) {
							tmp.add(w);
							break;
						}
					}
				} else if (search.startsWith("$")) {
					StringBuilder builder = new StringBuilder();
					for (int oreId : OreDictionary.getOreIDs(w.getStack())) {
						String oreName = OreDictionary.getOreName(oreId);
						builder.append(oreName).append(' ');
					}
					if (builder.toString().toLowerCase().contains(search.substring(1)))
						tmp.add(w);
				} else if (search.startsWith("%")) {
					StringBuilder builder = new StringBuilder();
					for (CreativeTabs creativeTab : w.getStack().getItem().getCreativeTabs()) {
						if (creativeTab != null) {
							String creativeTabName = creativeTab.getTranslatedTabLabel();
							builder.append(creativeTabName).append(' ');
						}
					}
					if (builder.toString().toLowerCase().contains(search.substring(1)))
						tmp.add(w);
				} else if (TextFormatting.getTextWithoutFormattingCodes(w.getStack().getDisplayName()).toLowerCase().contains(search))
					tmp.add(w);

			}
		Collections.sort(tmp, new Comparator<StackWrapper>() {
			int mul = !tile.topDown ? -1 : 1;

			@Override
			public int compare(StackWrapper o2, StackWrapper o1) {
				switch (tile.sort) {
				case AMOUNT:
					return Integer.compare(o1.getSize(), o2.getSize()) * mul;
				case NAME:
					return TextFormatting.getTextWithoutFormattingCodes(o2.getStack().getDisplayName()).compareToIgnoreCase(TextFormatting.getTextWithoutFormattingCodes(o1.getStack().getDisplayName())) * mul;
				case MOD:
					return Utils.getModName(o2.getStack().getItem()).compareToIgnoreCase(Utils.getModName(o1.getStack().getItem())) * mul;
				}
				return 0;
			}
		});
		return tmp;
	}
}
