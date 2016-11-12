package mrriegel.storagenetwork.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.storagenetwork.Enums.IOMODE;
import mrriegel.storagenetwork.container.ContainerItemConnect;
import mrriegel.storagenetwork.tile.TileNetworkItemConnection;
import mrriegel.storagenetwork.tile.TileNetworkStorage;

import com.google.common.collect.Lists;

public class GuiItemConnect extends CommonGuiContainer {

	GuiButtonSimple button;
	TileNetworkItemConnection tile;

	public GuiItemConnect(ContainerItemConnect inventorySlotsIn) {
		super(inventorySlotsIn);
		ySize = 114;
		tile = inventorySlotsIn.tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (tile instanceof TileNetworkStorage)
			buttonList.add(button = new GuiButtonSimple(0, guiLeft + 7, guiTop + 7, 20, 20, "", null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(7, 31);
		drawer.drawSlot(79, 8);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (button != null) {
			button.displayString = ((TileNetworkStorage) tile).iomode == IOMODE.IN ? "IN" : ((TileNetworkStorage) tile).iomode == IOMODE.OUT ? "OUT" : "IO";
			button.setTooltip(((TileNetworkStorage) tile).iomode == IOMODE.IN ? "Items will only be inserted into the network." : ((TileNetworkStorage) tile).iomode == IOMODE.OUT ? "Items will only be extracted from the network." : "Items will be inserted and extracted.");
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (!inventorySlots.getSlot(0).getHasStack() && isPointInRegion(inventorySlots.getSlot(0).xDisplayPosition, inventorySlots.getSlot(0).yDisplayPosition, 16, 16, mouseX, mouseY))
			drawHoveringText(Lists.newArrayList("Drop Item Filter here."), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		tile.sendMessage(new NBTTagCompound());
		tile.handleMessage(null, null);
	}

}
