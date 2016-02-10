package mrriegel.storagenetwork.proxy;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.render.CableModel;
import mrriegel.storagenetwork.render.ClientEventHandlers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandlers());
		OBJLoader.instance.addDomain(StorageNetwork.MODID);
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
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		mesher.register(Item.getItemFromBlock(ModBlocks.kabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":kabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.exKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":exKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.storageKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":storageKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.imKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":imKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.vacuumKabel), 0, new ModelResourceLocation(StorageNetwork.MODID + ":vacuumKabel", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.master), 0, new ModelResourceLocation(StorageNetwork.MODID + ":master", "inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.request), 0, new ModelResourceLocation(StorageNetwork.MODID + ":request", "inventory"));
		for (int i = 0; i < ItemUpgrade.num; i++) {
			ModelBakery.registerItemVariants(ModItems.upgrade, new ResourceLocation(StorageNetwork.MODID + ":upgrade_" + i));
			mesher.register(ModItems.upgrade, i, new ModelResourceLocation(StorageNetwork.MODID + ":upgrade_" + i, "inventory"));
		}
		for (int i = 0; i < 2; i++) {
			ModelBakery.registerItemVariants(ModItems.remote, new ResourceLocation(StorageNetwork.MODID + ":remote_" + i));
			mesher.register(ModItems.remote, i, new ModelResourceLocation(StorageNetwork.MODID + ":remote_" + i, "inventory"));
		}

	}

	public void registerRenderers() {
		ModelLoader.setCustomStateMapper(ModBlocks.kabel, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
				return CableModel.kabel;
			}
		});

		// ModelLoader.setCustomStateMapper(ModBlocks.exKabel, new
		// StateMapperBase() {
		// @Override
		// protected ModelResourceLocation getModelResourceLocation(IBlockState
		// iBlockState) {
		// return CableModel.ex;
		// }
		// });
		// ModelLoader.setCustomStateMapper(ModBlocks.imKabel, new
		// StateMapperBase() {
		// @Override
		// protected ModelResourceLocation getModelResourceLocation(IBlockState
		// iBlockState) {
		// return CableModel.im;
		// }
		// });
		// ModelLoader.setCustomStateMapper(ModBlocks.storageKabel, new
		// StateMapperBase() {
		// @Override
		// protected ModelResourceLocation getModelResourceLocation(IBlockState
		// iBlockState) {
		// return CableModel.storage;
		// }
		// });
		// ModelLoader.setCustomStateMapper(ModBlocks.vacuumKabel, new
		// StateMapperBase() {
		// @Override
		// protected ModelResourceLocation getModelResourceLocation(IBlockState
		// iBlockState) {
		// return CableModel.vacuum;
		// }
		// });
	}

}
