package mrriegel.storagenetwork.proxy;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.render.CableRenderer;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
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
		registerItemModels();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(ModItems.toggler);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.kabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":kabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.exKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":exKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.storageKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":storageKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.imKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":imKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.vacuumKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":vacuumKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.fexKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fexKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.fstorageKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fstorageKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.fimKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fimKabel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.master), 0, new ModelResourceLocation(StorageNetwork.MODID + ":master", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.request), 0, new ModelResourceLocation(StorageNetwork.MODID + ":request", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.frequest), 0, new ModelResourceLocation(StorageNetwork.MODID + ":frequest", "inventory"));
		for (int i = 0; i < ItemUpgrade.NUM; i++) {
			ModelBakery.registerItemVariants(ModItems.upgrade, new ResourceLocation(StorageNetwork.MODID + ":upgrade_" + i));
			ModelLoader.setCustomModelResourceLocation(ModItems.upgrade, i, new ModelResourceLocation(StorageNetwork.MODID + ":upgrade_" + i, "inventory"));
		}
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.remote, new ResourceLocation(StorageNetwork.MODID + ":remote_" + i));
			ModelLoader.setCustomModelResourceLocation(ModItems.remote, i, new ModelResourceLocation(StorageNetwork.MODID + ":remote_" + i, "inventory"));
		}
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.fremote, new ResourceLocation(StorageNetwork.MODID + ":fremote_" + i));
			ModelLoader.setCustomModelResourceLocation(ModItems.fremote, i, new ModelResourceLocation(StorageNetwork.MODID + ":fremote_" + i, "inventory"));
		}
		ModelLoader.setCustomModelResourceLocation(ModItems.coverstick, 0, new ModelResourceLocation(StorageNetwork.MODID + ":coverstick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.cover), 0, new ModelResourceLocation(StorageNetwork.MODID + ":cover", "inventory"));
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.template, new ResourceLocation(StorageNetwork.MODID + ":template_" + i));
			ModelLoader.setCustomModelResourceLocation(ModItems.template, i, new ModelResourceLocation(StorageNetwork.MODID + ":template_" + i, "inventory"));
		}
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.indicator), 0, new ModelResourceLocation(StorageNetwork.MODID + ":indicator", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.toggler, 0, new ModelResourceLocation(StorageNetwork.MODID + ":toggler", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.annexer), 0, new ModelResourceLocation(StorageNetwork.MODID + ":annexer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.fannexer), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fannexer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.itemBox), 0, new ModelResourceLocation(StorageNetwork.MODID + ":itemBox", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.fluidBox), 0, new ModelResourceLocation(StorageNetwork.MODID + ":fluidBox", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.duplicator, 0, new ModelResourceLocation(StorageNetwork.MODID + ":duplicator", "inventory"));
	}

	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileKabel.class, new CableRenderer());
	}

}
