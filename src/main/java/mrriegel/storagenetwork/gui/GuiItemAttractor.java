package mrriegel.storagenetwork.gui;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.storagenetwork.container.ContainerItemAttractor;
import mrriegel.storagenetwork.tile.TileItemAttractor;

import com.google.common.collect.Lists;

public class GuiItemAttractor extends CommonGuiContainer {

	TileItemAttractor tile;

	public GuiItemAttractor(ContainerItemAttractor inventorySlotsIn) {
		super(inventorySlotsIn);
		ySize = 114;
		tile = inventorySlotsIn.tile;
	}

	@Override
	public void initGui() {
		super.initGui();
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
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (!inventorySlots.getSlot(0).getHasStack() && isPointInRegion(inventorySlots.getSlot(0).xDisplayPosition, inventorySlots.getSlot(0).yDisplayPosition, 16, 16, mouseX, mouseY)) {
			drawHoveringText(Lists.newArrayList("Drop Item Filter here."), mouseX - guiLeft, mouseY - guiTop);
		}
	}

}
