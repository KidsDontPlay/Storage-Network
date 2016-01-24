package mrriegel.storagenetwork.proxy;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
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

	}

}
