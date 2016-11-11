package mrriegel.storagenetwork.gui;

import java.awt.Color;

import mrriegel.limelib.gui.CommonGuiScreen;
import mrriegel.storagenetwork.tile.TileNetworkCore;

public class GuiNetworkCore extends CommonGuiScreen{

	TileNetworkCore core;

	public GuiNetworkCore(TileNetworkCore core) {
		this.core = core;
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		drawer.drawBackgroundTexture();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawEnergyBarV(10, 10, 80, (float) core.getEnergyStored(null) / (float) core.getMaxEnergyStored(null));
		drawString(fontRendererObj, core.getEnergyStored(null)+" RF", guiLeft+50, guiTop+10, Color.white.getRGB());
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
}
