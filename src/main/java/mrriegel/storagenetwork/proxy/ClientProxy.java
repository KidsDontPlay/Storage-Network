package mrriegel.storagenetwork.proxy;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.render.CableRenderer;
import mrriegel.storagenetwork.tile.TileIndicator;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
		MinecraftForge.EVENT_BUS.register(ModItems.toggler);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemModels() {
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		mesher.register(Item.getItemFromBlock(ModBlocks.kabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":kabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.exKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":exKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.storageKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":storageKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.imKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":imKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.vacuumKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":vacuumKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.fexKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fexKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.fstorageKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fstorageKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.fimKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fimKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.master), 0, new ModelResourceLocation(StorageNetwork.MODID + ":master", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.request), 0, new ModelResourceLocation(StorageNetwork.MODID + ":request", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.frequest), 0, new ModelResourceLocation(StorageNetwork.MODID + ":frequest", "inventory"));
		for (int i = 0; i < ItemUpgrade.NUM; i++) {
			ModelBakery.registerItemVariants(ModItems.upgrade, new ResourceLocation(StorageNetwork.MODID + ":upgrade_" + i));
			mesher.register(ModItems.upgrade, i, new ModelResourceLocation(StorageNetwork.MODID + ":upgrade_" + i, "inventory"));
		}
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.remote, new ResourceLocation(StorageNetwork.MODID + ":remote_" + i));
			mesher.register(ModItems.remote, i, new ModelResourceLocation(StorageNetwork.MODID + ":remote_" + i, "inventory"));
		}
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.fremote, new ResourceLocation(StorageNetwork.MODID + ":fremote_" + i));
			mesher.register(ModItems.fremote, i, new ModelResourceLocation(StorageNetwork.MODID + ":fremote_" + i, "inventory"));
		}
		mesher.register(ModItems.coverstick, 0, new ModelResourceLocation(StorageNetwork.MODID + ":coverstick", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.cover), 0, new ModelResourceLocation(StorageNetwork.MODID + ":cover", "inventory"));
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.template, new ResourceLocation(StorageNetwork.MODID + ":template_" + i));
			mesher.register(ModItems.template, i, new ModelResourceLocation(StorageNetwork.MODID + ":template_" + i, "inventory"));
		}
		mesher.register(Item.getItemFromBlock(ModBlocks.indicator), 0, new ModelResourceLocation(StorageNetwork.MODID + ":indicator", "inventory"));
		mesher.register(ModItems.toggler, 0, new ModelResourceLocation(StorageNetwork.MODID + ":toggler", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.annexer), 0, new ModelResourceLocation(StorageNetwork.MODID + ":annexer", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.fannexer), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fannexer", "inventory"));
	}

	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileKabel.class, new CableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileIndicator.class, new T());
	}

	static class T extends TileEntitySpecialRenderer<TileIndicator> {

		@Override
		public void renderTileEntityAt(TileIndicator te, double x, double y, double z, float partialTicks, int destroyStage) {

			// GlStateManager.translate((float) x + 0.5F, (float) y + 2.5F,
			// (float) z + 0.5F);
			// GlStateManager.pushMatrix();
			// GlStateManager.rotate(180F, 0.0F, 1.0F, 1.0F);
			// Minecraft mc = Minecraft.getMinecraft();
			// mc.getRenderItem().renderItemIntoGUI(new
			// ItemStack(Items.emerald), 0, 0);
			// mc.getRenderItem().renderItem(new ItemStack(Items.wheat),
			// TransformType.FIXED);
			// FontRenderer f=getFontRenderer();
			// f.drawString("splitscrren", 0 , 0, 546193);
			// GlStateManager.popMatrix();
			
			// int ambientLight = (int)
			// te.getWorld().getLightBrightness(te.getPos());
			// int var6 = ambientLight % 65536;
			// int var7 = ambientLight / 65536;
			// float var8 = 1.0F;
			// OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
			// var6 * var8, var7 * var8);
			// GlStateManager.pushMatrix();
			// ForgeHooksClient.renderTileItem(Items.emerald, 0);
			// Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(new
			// ItemStack(Items.emerald), 0, 0);
			// GlStateManager.popMatrix();
		}

	}

}
