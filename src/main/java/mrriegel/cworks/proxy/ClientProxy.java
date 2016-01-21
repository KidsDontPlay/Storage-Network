package mrriegel.cworks.proxy;

import mrriegel.cworks.CableWorks;
import mrriegel.cworks.init.ModBlocks;
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
				new ModelResourceLocation(CableWorks.MODID + ":exKabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.kabel), 0,
				new ModelResourceLocation(CableWorks.MODID + ":kabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.storageKabel), 0,
				new ModelResourceLocation(CableWorks.MODID + ":storageKabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.imKabel), 0,
				new ModelResourceLocation(CableWorks.MODID + ":imKabel",
						"inventory"));
		mesher.register(Item.getItemFromBlock(ModBlocks.vacuumKabel), 0,
				new ModelResourceLocation(CableWorks.MODID + ":vacuumKabel",
						"inventory"));

	}

	public void registerRenderers() {

	}

}
