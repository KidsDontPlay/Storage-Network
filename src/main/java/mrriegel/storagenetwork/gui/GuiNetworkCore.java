package mrriegel.storagenetwork.gui;

import java.awt.Color;
import java.util.List;

import mrriegel.limelib.gui.CommonGuiScreen;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class GuiNetworkCore extends CommonGuiScreen {

	public TileNetworkCore core;
	public NBTTagCompound data;

	public GuiNetworkCore(TileNetworkCore core) {
		this.core = core;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		drawer.drawBackgroundTexture();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawColoredRectangle(9, 9, 10, 103, Color.BLACK.getRGB());
		drawer.drawFramedRectangle(26, 9, 130, 128);
		drawer.drawEnergyBarV(10, 10, 100, (float) core.getEnergyStored(null) / (float) core.getMaxEnergyStored(null));
		if (data != null && data.getInteger("maxcell") > 0) {
			drawer.drawColoredRectangle(9, 139, 103, 10, Color.BLACK.getRGB());
			drawer.drawEnergyBarH(10, 140, 100, (float) data.getInteger("cell") / (float) data.getInteger("maxcell"));
		}
		if (data != null) {
			List<String> lis = Lists.newArrayList("Network size: " + data.getInteger("nsize"));
			lis.addAll(NBTHelper.getStringList(data, "parts"));
			boolean uni = fontRendererObj.getUnicodeFlag();
			fontRendererObj.setUnicodeFlag(true);
			for (int i = 0; i < 16; i++)
				if (lis.size() <= i)
					break;
				else
					this.fontRendererObj.drawString(lis.get(i), guiLeft + 30, guiTop + 10 + i * 8, 0);
			fontRendererObj.setUnicodeFlag(uni);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (isPointInRegion(10, 10, 8, 100, mouseX, mouseY))
			drawHoveringText(Lists.newArrayList(core.getEnergyStored(null) + "/" + core.getMaxEnergyStored(null) + " RF", data.getInteger("transfer") + " RF/t"), mouseX - guiLeft, mouseY - guiTop);
		if (isPointInRegion(10, 140, 100, 8, mouseX, mouseY) && data != null)
			drawHoveringText(Lists.newArrayList(data.getInteger("cell") + "/" + data.getInteger("maxcell") + " RF in Energy Cells"), mouseX - guiLeft, mouseY - guiTop);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
