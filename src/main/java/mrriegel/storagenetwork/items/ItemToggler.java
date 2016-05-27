package mrriegel.storagenetwork.items;

import java.awt.Color;
import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class ItemToggler extends Item {

	public ItemToggler() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setRegistryName("toggler");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileKabel) {
			TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
			tile.setDisabled(!tile.isDisabled());
			if (tile.getMaster() != null && worldIn.getTileEntity(tile.getMaster()) instanceof TileMaster) {
				TileMaster mas = ((TileMaster) worldIn.getTileEntity(tile.getMaster()));
				for (BlockPos p : mas.connectables)
					((IConnectable) worldIn.getTileEntity(p)).setMaster(null);
				mas.refreshNetwork();
			} else {
				for (BlockPos p : Util.getSides(pos)) {
					if (worldIn.getTileEntity(p) instanceof IConnectable && ((IConnectable) worldIn.getTileEntity(p)).getMaster() != null) {
						TileMaster mas = ((TileMaster) worldIn.getTileEntity(((IConnectable) worldIn.getTileEntity(p)).getMaster()));
						for (BlockPos pp : mas.connectables)
							((IConnectable) worldIn.getTileEntity(pp)).setMaster(null);
						mas.refreshNetwork();
						break;
					}
				}
			}
			Util.updateTile(worldIn, pos);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	List<BlockPos> lis = Lists.newArrayList();

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != this)
			return;
		if (player.worldObj.getTotalWorldTime() % 10 == 0) {
			lis = Lists.newArrayList();
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
		}
		double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

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
			VertexBuffer renderer = tessellator.getBuffer();
			renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
			GL11.glLineWidth(2.5f);
			GlStateManager.pushAttrib();
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
			GlStateManager.popAttrib();
			// RenderHelper.disableStandardItemLighting();

		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);

		GlStateManager.popMatrix();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("tooltip.storagenetwork.toggler"));
	}

}
