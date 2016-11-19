package mrriegel.storagenetwork.render;

import java.awt.Color;

import mrriegel.limelib.util.Utils;
import mrriegel.storagenetwork.tile.TileItemMirror;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TESRItemMirror extends TileEntitySpecialRenderer<TileItemMirror> {

	@Override
	public void renderTileEntityAt(TileItemMirror te, double x, double y, double z, float partialTicks, int destroyStage) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem itemRenderer = mc.getRenderItem();

		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(x + .5, y + .5, z + .5);
		GlStateManager.rotate(180F, 1, 0, 0);

		switch (te.face) {
		case EAST:
			GlStateManager.rotate(270, 0, 1, 0);
			GlStateManager.translate(0, 0, -.501);
			break;
		case NORTH:
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(0, 0, -.501);
			break;
		case SOUTH:
			//no rotation
			GlStateManager.translate(0, 0, -.501);
			break;
		case WEST:
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(0, 0, -.501);
			break;
		default:
			break;

		}
		GlStateManager.scale(0.025f, 0.025f, -0.0001f);
		float n = -300F;
		GlStateManager.translate(0, 0, -n);
		boolean uni = mc.fontRendererObj.getUnicodeFlag();
		mc.fontRendererObj.setUnicodeFlag(true);
		mc.fontRendererObj.drawString(Utils.formatNumber(999) + " dsxW", -10, 2, Color.white.getRGB());
		mc.fontRendererObj.setUnicodeFlag(uni);
		GlStateManager.translate(0, 0, n);

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		if (te.topLeft != null && te.topLeft.getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.topLeft.getStack(), -17, -17);
		if (te.topRight != null && te.topRight.getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.topRight.getStack(), 1, -17);
		if (te.bottomLeft != null && te.bottomLeft.getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.bottomLeft.getStack(), -17, 1);
		if (te.bottomRight != null && te.bottomRight.getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.bottomRight.getStack(), 1, 1);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

}
