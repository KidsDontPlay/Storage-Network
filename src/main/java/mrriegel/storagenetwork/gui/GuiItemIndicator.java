package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.io.IOException;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.storagenetwork.container.ContainerItemIndicator;
import mrriegel.storagenetwork.tile.TileItemIndicator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.StringUtils;

public class GuiItemIndicator extends CommonGuiContainer {

	GuiButtonSimple mode;
	TileItemIndicator tile;
	GuiTextField textField;

	public GuiItemIndicator(ContainerItemIndicator inventorySlotsIn) {
		super(inventorySlotsIn);
		ySize = 114;
		tile = inventorySlotsIn.tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(mode = new GuiButtonSimple(0, guiLeft + 100, guiTop + 7, 20, 20, "", Color.BLACK.getRGB(), Color.GRAY.getRGB(), null));
		textField = new GuiTextField(0, fontRendererObj, guiLeft + 30, guiTop + 12, 65, fontRendererObj.FONT_HEIGHT);
		textField.setMaxStringLength(8);
		textField.setEnableBackgroundDrawing(!false);
		textField.setVisible(true);
		textField.setTextColor(16777215);
		textField.setFocused(true);
		textField.setText("" + tile.number);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawPlayerSlots(7, 31);
		drawer.drawSlot(7, 8);
		textField.drawTextBox();
		fontRendererObj.drawString("Total", guiLeft + 125, guiTop + 13, Color.darkGray.getRGB());
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		mode.displayString = tile.more ? ">" : "<=";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!this.checkHotbarKeys(keyCode)) {
			if ((StringUtils.isNumeric(typedChar + "") || typedChar == 8) && this.textField.textboxKeyTyped(typedChar, keyCode)) {
				try {
					tile.number = Integer.valueOf(textField.getText());
				} catch (NumberFormatException e) {
					tile.number = 0;
				}
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("text", textField.getText());
				nbt.setInteger("buttonID", 1000);
				tile.sendMessage(nbt);
			}
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("buttonID", button.id);
		tile.sendMessage(nbt);
		tile.handleMessage(mc.thePlayer, nbt);
	}

}
