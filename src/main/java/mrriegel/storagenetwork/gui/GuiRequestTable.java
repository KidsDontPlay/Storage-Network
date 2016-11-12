package mrriegel.storagenetwork.gui;

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
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
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
	protected List<ItemSlot> items;
	protected boolean craftingTable;
	protected long lastClick;
	protected GuiButton sort, direction;
	protected GuiTextField searchBar;
	protected int currentPos = 0, maxPos = 0;
	protected ItemStack over;

	protected boolean canClick() {
		return System.currentTimeMillis() > lastClick + 400L;
	}

	public GuiRequestTable(ContainerRequestTable inventorySlotsIn) {
		super(inventorySlotsIn);
		craftingTable = inventorySlotsIn.isCrafting;
		tile = inventorySlotsIn.tile;
		ySize = 220;
		xSize += 36 + 36;
		lastClick = System.currentTimeMillis();
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
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(79, 137);
		drawer.drawSlots(7, 7, 13, 6);
		//		drawer.drawTextfield(searchBar);
		searchBar.drawTextBox();
		if (craftingTable) {
			drawer.drawSlots(7, 137, 3, 3);
			drawer.drawProgressArrow(13, 196, 0F, Direction.RIGHT);
			drawer.drawSlot(43, 195);
		}
		over=null;
		if (wrappers != null) {
			
			List<StackWrapper> tmp = getFilteredList();
			for (int i = 0; i < Math.min(tmp.size(), 8); i++) {
				ItemSlot s = new ItemSlot(tmp.get(i).getStack(), 0, guiLeft + 8 + i * 18, guiTop + 8, 1, drawer, true, true, !true, true);
				s.draw(mouseX, mouseY);
				
				if (s.isMouseOver(mouseX, mouseY)){
					over=s.stack;
					s.drawTooltip(mouseX, mouseY);}
			}
			;
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (searchBar.isFocused() && Loader.isModLoaded("JEI") && Internal.getRuntime().getItemListOverlay().hasKeyboardFocus()) {
			searchBar.setFocused(false);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		NBTTagCompound nbt = new NBTTagCompound();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchBar.setFocused(isPointInRegion(searchBar.xPosition, searchBar.yPosition, searchBar.width, searchBar.height, mouseX + guiLeft, mouseY + guiTop));
		if (searchBar.isFocused() && mouseButton == 1)
			searchBar.setText("");
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
				//				PacketHandler.INSTANCE.sendToServer(new RequestMessage(0, null, false, false));
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
