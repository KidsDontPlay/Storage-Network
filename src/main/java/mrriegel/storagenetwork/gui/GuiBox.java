package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.io.IOException;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.storagenetwork.Enums.IOMODE;
import mrriegel.storagenetwork.container.ContainerBox;
import mrriegel.storagenetwork.tile.TileBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class GuiBox extends CommonGuiContainer {

	GuiButtonSimple io;
	TileBox<?, ?> tile;

	public GuiBox(ContainerBox inventorySlotsIn) {
		super(inventorySlotsIn);
		ySize = 114;
		tile = inventorySlotsIn.tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonSimple(0, guiLeft + 106, guiTop + 7, 10, 20, "-", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("-Priority")));
		buttonList.add(new GuiButtonSimple(1, guiLeft + 135, guiTop + 7, 10, 20, "+", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("+Priority")));
		buttonList.add(io = new GuiButtonSimple(2, guiLeft + 150, guiTop + 7, 20, 20, "", Color.BLACK.getRGB(), Color.GRAY.getRGB(), null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(7, 31);
		drawer.drawSlot(7, 8);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		io.displayString = tile.iomode == IOMODE.IN ? "IN" : tile.iomode == IOMODE.OUT ? "OUT" : "IO";
		io.setTooltip(tile.iomode == IOMODE.IN ? "Items will only be inserted into the network." : tile.iomode == IOMODE.OUT ? "Items will only be extracted from the network." : "Items will be inserted and extracted.");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String p = "" + tile.getPriority();
		fontRendererObj.drawString(p, 126 - fontRendererObj.getStringWidth(p) / 2, 14, Color.DARK_GRAY.getRGB(), false);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (!inventorySlots.getSlot(0).getHasStack() && isPointInRegion(inventorySlots.getSlot(0).xDisplayPosition, inventorySlots.getSlot(0).yDisplayPosition, 16, 16, mouseX, mouseY)) {
			drawHoveringText(Lists.newArrayList("Drop Item Filter here."), mouseX - guiLeft, mouseY - guiTop);
		}
		for (int i = 1; i < 5; i++)
			if (!inventorySlots.getSlot(i).getHasStack() && isPointInRegion(inventorySlots.getSlot(i).xDisplayPosition, inventorySlots.getSlot(i).yDisplayPosition, 16, 16, mouseX, mouseY))
				drawHoveringText(Lists.newArrayList("Drop Upgrades here."), mouseX - guiLeft, mouseY - guiTop);
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
