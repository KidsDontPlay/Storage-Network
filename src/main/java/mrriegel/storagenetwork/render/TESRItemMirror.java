package mrriegel.storagenetwork.render;

import java.awt.Color;

import mrriegel.limelib.util.Utils;
import mrriegel.storagenetwork.tile.TileItemMirror;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class TESRItemMirror extends TileEntitySpecialRenderer<TileItemMirror> {

	@Override
	public void renderTileEntityAt(TileItemMirror te, double x, double y, double z, float partialTicks, int destroyStage) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem itemRenderer = mc.getRenderItem();
		if (new BlockPos(mc.thePlayer).getDistance(te.getX(), te.getY(), te.getZ()) > 32)
			return;
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

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		if (te.wraps.get(0) != null && te.wraps.get(0).getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.wraps.get(0).getStack(), -17, -17);
		if (te.wraps.get(1) != null && te.wraps.get(1).getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.wraps.get(1).getStack(), 1, -17);
		if (te.wraps.get(2) != null && te.wraps.get(2).getStack().getItem() != null)
			itemRenderer.renderItemIntoGUI(te.wraps.get(2).getStack(), -17, 1);
		if (te.wraps.get(3) != null && te.wraps.get(3).getStack().getItem() != null) {
			itemRenderer.renderItemIntoGUI(te.wraps.get(3).getStack(), 1, 1);
		}
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();

		float n = -200F;
		GlStateManager.translate(0, 0, -n);
		boolean uni = mc.fontRendererObj.getUnicodeFlag();
		mc.fontRendererObj.setUnicodeFlag(true);
		
		GlStateManager.disableLighting();
//		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		if (te.wraps.get(0) != null && te.wraps.get(0).getStack().getItem() != null) {
			mc.fontRendererObj.drawString(TextFormatting.BOLD + Utils.formatNumber(te.wraps.get(0).getSize()), -17, -17 + 7, Color.white.getRGB(), !true);
		}
		if (te.wraps.get(1) != null && te.wraps.get(1).getStack().getItem() != null) {
			mc.fontRendererObj.drawString(TextFormatting.BOLD + Utils.formatNumber(te.wraps.get(1).getSize()), 1, -17 + 7, Color.white.getRGB(), !true);
		}
		if (te.wraps.get(2) != null && te.wraps.get(2).getStack().getItem() != null) {
			mc.fontRendererObj.drawString(TextFormatting.BOLD + Utils.formatNumber(te.wraps.get(2).getSize()), -17, 1 + 7, Color.white.getRGB(), !true);
		}
		if (te.wraps.get(3) != null && te.wraps.get(3).getStack().getItem() != null) {
			mc.fontRendererObj.drawString(TextFormatting.BOLD + Utils.formatNumber(te.wraps.get(3).getSize()), 1, 1 + 7, Color.white.getRGB(), !true);
		}
		GlStateManager.enableLighting();
//		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		mc.fontRendererObj.setUnicodeFlag(uni);

		GlStateManager.translate(0, 0, n);

		GlStateManager.popMatrix();
	}

}
