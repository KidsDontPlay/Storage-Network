package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.io.IOException;

import com.google.common.collect.Lists;

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
		this.xSize = 80;
		this.ySize = 40;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonSimple(0, guiLeft + 36, guiTop + 10, 10, 18, "-", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("-Priority")));
		buttonList.add(new GuiButtonSimple(1, guiLeft + 65, guiTop + 10, 10, 18, "+", Color.BLACK.getRGB(), Color.GRAY.getRGB(), Lists.newArrayList("+Priority")));
		buttonList.add(button = new GuiButtonSimple(2, guiLeft + 9, guiTop + 10, 22, 18, "", null));
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
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String p = "" + tile.getPriority();
		fontRendererObj.drawString(p, 56 - fontRendererObj.getStringWidth(p) / 2, 15, Color.DARK_GRAY.getRGB(), false);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
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

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
