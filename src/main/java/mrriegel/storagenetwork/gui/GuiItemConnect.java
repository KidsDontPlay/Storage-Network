package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.io.IOException;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.storagenetwork.Enums.IOMODE;
import mrriegel.storagenetwork.container.ContainerItemConnect;
import mrriegel.storagenetwork.tile.TileNetworkExporter;
import mrriegel.storagenetwork.tile.TileNetworkImporter;
import mrriegel.storagenetwork.tile.TileNetworkItemConnection;
import mrriegel.storagenetwork.tile.TileNetworkStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class GuiItemConnect extends CommonGuiContainer {

	GuiButtonSimple io;
	TileNetworkItemConnection tile;

	public GuiItemConnect(ContainerItemConnect inventorySlotsIn) {
		super(inventorySlotsIn);
		ySize = 114;
		tile = inventorySlotsIn.tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonSimple(0, guiLeft + 106, guiTop + 7, 10, 20, "-", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("-Priority")));
		buttonList.add(new GuiButtonSimple(1, guiLeft + 135, guiTop + 7, 10, 20, "+", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("+Priority")));
		if (tile instanceof TileNetworkStorage)
			buttonList.add(io = new GuiButtonSimple(2, guiLeft + 150, guiTop + 7, 20, 20, "", Color.BLACK.getRGB(), Color.GRAY.getRGB(), null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(7, 31);
		drawer.drawSlot(7, 8);
		if (tile instanceof TileNetworkExporter || tile instanceof TileNetworkImporter)
			drawer.drawSlots(29, 8, 4, 1);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (io != null) {
			io.displayString = ((TileNetworkStorage) tile).iomode == IOMODE.IN ? "IN" : ((TileNetworkStorage) tile).iomode == IOMODE.OUT ? "OUT" : "IO";
			io.setTooltip(((TileNetworkStorage) tile).iomode == IOMODE.IN ? "Items will only be inserted into the network." : ((TileNetworkStorage) tile).iomode == IOMODE.OUT ? "Items will only be extracted from the network." : "Items will be inserted and extracted.");
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String p = "" + tile.getPriority();
		fontRendererObj.drawString(p, 126 - fontRendererObj.getStringWidth(p) / 2, 14, Color.DARK_GRAY.getRGB(), false);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (!inventorySlots.getSlot(0).getHasStack() && isPointInRegion(inventorySlots.getSlot(0).xDisplayPosition, inventorySlots.getSlot(0).yDisplayPosition, 16, 16, mouseX, mouseY)) {
			drawHoveringText(Lists.newArrayList("Drop Item Filter here."), mouseX - guiLeft, mouseY - guiTop);
//			new AbstractSlot.FluidSlot(FluidRegistry.WATER, 0, mouseX, mouseY, 1, drawer, false, false, false, false).draw(mouseX-guiLeft, mouseY-guiTop);
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
