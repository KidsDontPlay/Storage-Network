package mrriegel.storagenetwork.proxy;

import java.util.Random;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.lwjgl.opengl.GL11;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		registerRenderers();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		registerItemModels();

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemModels() {
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem()
				.getItemModelMesher();
		mesher.register(Item.getItemFromBlock(ModBlocks.exKabel), 0,
				new ModelResourceLocation(StorageNetwork.MODID + ":exKabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.kabel), 0,
				new ModelResourceLocation(StorageNetwork.MODID + ":kabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.storageKabel), 0,
				new ModelResourceLocation(StorageNetwork.MODID
						+ ":storageKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.imKabel), 0,
				new ModelResourceLocation(StorageNetwork.MODID + ":imKabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.vacuumKabel), 0,
				new ModelResourceLocation(
						StorageNetwork.MODID + ":vacuumKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.master), 0,
				new ModelResourceLocation(StorageNetwork.MODID + ":master",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.request), 0,
				new ModelResourceLocation(StorageNetwork.MODID + ":request",
						"inventory"));

	}

	public void registerRenderers() {
//		ClientRegistry.bindTileEntitySpecialRenderer(TileKabel.class, new R());
	}

	class R extends TileEntitySpecialRenderer<TileKabel> {

		@Override
		public void renderTileEntityAt(TileKabel te, double x, double y,
				double z, float partialTicks, int destroyStage) {
			if (te.getMaster() != null) {
				float timeD = (float) (360.0 * (double) (System
						.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL) * 8;
				float scale = 1f;
				GL11.glPushMatrix();
				GL11.glTranslated(x + .5, y + 1D, z + .5);
				GlStateManager.rotate(timeD, 0.0F, 1.0F, 0.0F);
				ItemStack i = new ItemStack(Items.ghast_tear);
				EntityItem item = new EntityItem(te.getWorld(), x, y, z, i);
				GL11.glScalef(scale, scale, scale);
				item.setEntityItemStack(i);
				item.hoverStart = 0.0F;
				new RenderEntityItem(Minecraft.getMinecraft()
						.getRenderManager(), Minecraft.getMinecraft()
						.getRenderItem()).doRender(item, 0, 0, 0, 0, 0);
				GL11.glPopMatrix();
			}
		}

	}

}
