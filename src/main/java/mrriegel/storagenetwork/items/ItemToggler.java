package mrriegel.storagenetwork.items;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

public class ItemToggler extends Item {

	public ItemToggler() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":toggler");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileKabel) {
			TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
			tile.setDisabled(!tile.isDisabled());
			if (tile.getMaster() != null && worldIn.getTileEntity(tile.getMaster()) instanceof TileMaster)
				((TileMaster) worldIn.getTileEntity(tile.getMaster())).refreshNetwork();
			else {
				for (BlockPos p : Util.getSides(pos)) {
					if (worldIn.getTileEntity(p) instanceof IConnectable && ((IConnectable) worldIn.getTileEntity(p)).getMaster() != null) {
						((TileMaster) worldIn.getTileEntity(((IConnectable) worldIn.getTileEntity(p)).getMaster())).refreshNetwork();
						break;
					}
				}
			}
			worldIn.markBlockForUpdate(pos);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (player.getHeldItem() == null || player.getHeldItem().getItem() != this)
			return;
		List<BlockPos> lis = new ArrayList<BlockPos>();
		int range = 16;
		for (int i = -range; i <= range; i++)
			for (int j = -range; j <= range; j++)
				for (int k = -range; k <= range; k++) {
					BlockPos pos = new BlockPos(i + player.posX, j + player.posY, k + player.posZ);
					if (!player.worldObj.isAirBlock(pos) && player.worldObj.getTileEntity(pos) instanceof TileKabel) {
						TileKabel tile = (TileKabel) player.worldObj.getTileEntity(pos);
						if (tile.isDisabled()) {
							lis.add(pos);
						}
					}
				}

		double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
		double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
		double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

		GlStateManager.pushMatrix();

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
		// code
		long c = (System.currentTimeMillis() / 15l) % 360l;
		// Color color = Color.getHSBColor(c / 360f, 1f, 1f);
		Color color = Color.RED;

		for (BlockPos p : lis) {
			float x = p.getX(), y = p.getY(), z = p.getZ();
			// RenderHelper.enableStandardItemLighting();
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer renderer = tessellator.getWorldRenderer();
			renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
			GL11.glLineWidth(2.5f);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			float offset = 1f;
			renderer.pos(x, y, z).endVertex();
			renderer.pos(x + offset, y, z).endVertex();

			renderer.pos(x, y, z).endVertex();
			renderer.pos(x, y + offset, z).endVertex();

			renderer.pos(x, y, z).endVertex();
			renderer.pos(x, y, z + offset).endVertex();

			renderer.pos(x + offset, y + offset, z + offset).endVertex();
			renderer.pos(x, y + offset, z + offset).endVertex();

			renderer.pos(x + offset, y + offset, z + offset).endVertex();
			renderer.pos(x + offset, y, z + offset).endVertex();

			renderer.pos(x + offset, y + offset, z + offset).endVertex();
			renderer.pos(x + offset, y + offset, z).endVertex();

			renderer.pos(x, y + offset, z).endVertex();
			renderer.pos(x, y + offset, z + offset).endVertex();

			renderer.pos(x, y + offset, z).endVertex();
			renderer.pos(x + offset, y + offset, z).endVertex();

			renderer.pos(x + offset, y, z).endVertex();
			renderer.pos(x + offset, y, z + offset).endVertex();

			renderer.pos(x + offset, y, z).endVertex();
			renderer.pos(x + offset, y + offset, z).endVertex();

			renderer.pos(x, y, z + offset).endVertex();
			renderer.pos(x + offset, y, z + offset).endVertex();

			renderer.pos(x, y, z + offset).endVertex();
			renderer.pos(x, y + offset, z + offset).endVertex();
			tessellator.draw();
			// RenderHelper.disableStandardItemLighting();

		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);

		GlStateManager.popMatrix();
	}

}
