package mrriegel.cworks.proxy;

import mrriegel.cworks.CableWorks;
import mrriegel.cworks.config.ConfigHandler;
import mrriegel.cworks.handler.GuiHandler;
import mrriegel.cworks.init.CraftingRecipes;
import mrriegel.cworks.init.ModBlocks;
import mrriegel.cworks.init.ModItems;
import mrriegel.cworks.network.PacketHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.refreshConfig(event.getSuggestedConfigurationFile());
		PacketHandler.init();

	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(CableWorks.instance,
				new GuiHandler());
		ModBlocks.init();
		ModItems.init();
		CraftingRecipes.init();
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
