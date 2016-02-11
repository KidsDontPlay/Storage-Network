package mrriegel.storagenetwork.render;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class CableRenderer extends TileEntitySpecialRenderer {

	ModelCable model;
	private final ResourceLocation link = new ResourceLocation(StorageNetwork.MODID + ":" + "textures/tile/link.png");
	private final ResourceLocation ex = new ResourceLocation(StorageNetwork.MODID + ":" + "textures/tile/ex.png");
	private final ResourceLocation im = new ResourceLocation(StorageNetwork.MODID + ":" + "textures/tile/im.png");
	private final ResourceLocation storage = new ResourceLocation(StorageNetwork.MODID + ":" + "textures/tile/storage.png");
	private final ResourceLocation vacuum = new ResourceLocation(StorageNetwork.MODID + ":" + "textures/tile/vacuum.png");

	public CableRenderer() {
		model = new ModelCable();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		switch (((TileKabel) te).getKind()) {
		case kabel:
			Minecraft.getMinecraft().renderEngine.bindTexture(link);
			break;
		case exKabel:
			Minecraft.getMinecraft().renderEngine.bindTexture(ex);
			break;
		case imKabel:
			Minecraft.getMinecraft().renderEngine.bindTexture(im);
			break;
		case storageKabel:
			Minecraft.getMinecraft().renderEngine.bindTexture(storage);
			break;
		case vacuumKabel:
			Minecraft.getMinecraft().renderEngine.bindTexture(vacuum);
			break;
		default:
			break;

		}
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		model.render((TileKabel) te);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}
}
