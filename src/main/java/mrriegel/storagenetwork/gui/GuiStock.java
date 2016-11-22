package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.storagenetwork.container.ContainerStock;
import mrriegel.storagenetwork.tile.TileNetworkStock;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class GuiStock extends CommonGuiContainer {

	TileNetworkStock tile;
	List<GuiTextField> textfields;

	public GuiStock(ContainerStock inventorySlotsIn) {
		super(inventorySlotsIn);
		tile = inventorySlotsIn.tile;
		ySize = 186;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonSimple(0, guiLeft + 106, guiTop + 7, 10, 20, "-", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("-Priority")));
		buttonList.add(new GuiButtonSimple(1, guiLeft + 135, guiTop + 7, 10, 20, "+", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("+Priority")));
		textfields = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			GuiTextField textField = new GuiTextField(i, fontRendererObj, guiLeft + 28, guiTop + 34 + 18 * i, 55, fontRendererObj.FONT_HEIGHT);
			textField.setMaxStringLength(8);
			textField.setEnableBackgroundDrawing(!false);
			textField.setVisible(true);
			textField.setTextColor(16777215);
			textField.setText("" + tile.numbers.get(i));
			textfields.add(textField);
		}
		for (int i = 0; i < 4; i++) {
			GuiTextField textField = new GuiTextField(i + 4, fontRendererObj, guiLeft + 108, guiTop + 34 + 18 * i, 55, fontRendererObj.FONT_HEIGHT);
			textField.setMaxStringLength(8);
			textField.setEnableBackgroundDrawing(!false);
			textField.setVisible(true);
			textField.setTextColor(16777215);
			textField.setText("" + tile.numbers.get(i + 4));
			textfields.add(textField);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(7, 103);
		drawer.drawSlots(29, 8, 4, 1);
		drawer.drawSlots(7, 29, 1, 4);
		drawer.drawSlots(86, 29, 1, 4);
		for (GuiTextField field : textfields)
			field.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String p = "" + tile.getPriority();
		fontRendererObj.drawString(p, 126 - fontRendererObj.getStringWidth(p) / 2, 14, Color.DARK_GRAY.getRGB(), false);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for (int i = 0; i < 4; i++)
			if (!inventorySlots.getSlot(i).getHasStack() && isPointInRegion(inventorySlots.getSlot(i).xDisplayPosition, inventorySlots.getSlot(i).yDisplayPosition, 16, 16, mouseX, mouseY))
				drawHoveringText(Lists.newArrayList("Drop Upgrades here."), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (GuiTextField field : textfields)
			field.setFocused(isPointInRegion(field.xPosition, field.yPosition, field.width, field.height, mouseX + guiLeft, mouseY + guiTop));
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!this.checkHotbarKeys(keyCode)) {
			for (GuiTextField field : textfields)
				if ((StringUtils.isNumeric(typedChar + "") || typedChar == 8) && field.textboxKeyTyped(typedChar, keyCode)) {
					try {
						tile.numbers.set(field.getId(), Integer.valueOf(field.getText()));
					} catch (NumberFormatException e) {
						tile.numbers.set(field.getId(), 0);
					}
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("text", field.getText());
					nbt.setInteger("index", field.getId());
					nbt.setInteger("buttonID", 1000);
					tile.sendMessage(nbt);
					return;
				}
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("shift", isShiftKeyDown());
		nbt.setInteger("buttonID", button.id);
		tile.sendMessage(nbt);
		tile.handleMessage(mc.thePlayer, nbt);
	}

}
