package mrriegel.storagenetwork.gui;

import java.io.IOException;

import mrriegel.limelib.gui.CommonGuiScreen;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.storagenetwork.Enums.IOMODE;
import mrriegel.storagenetwork.tile.TileNetworkEnergyInterface;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class GuiEnergyInterface extends CommonGuiScreen {

	TileNetworkEnergyInterface tile;
	GuiButtonSimple button;

	public GuiEnergyInterface(TileNetworkEnergyInterface tile) {
		this.tile = tile;
		this.xSize = 40;
		this.ySize = 40;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(button = new GuiButtonSimple(0, guiLeft + 9, guiTop + 10, 22, 18, "", null));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		button.displayString = tile.iomode == IOMODE.IN ? "IN" : tile.iomode == IOMODE.OUT ? "OUT" : "IO";
		button.setTooltip(tile.iomode == IOMODE.IN ? "Energy will only be inserted into the network." : tile.iomode == IOMODE.OUT ? "Energy will only be extracted from the network." : "Energy will be inserted and extracted.");
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		drawer.drawBackgroundTexture();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		tile.sendMessage(new NBTTagCompound());
		tile.handleMessage(null, null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
