package mrriegel.storagenetwork;

import mrriegel.storagenetwork.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
@Mod(modid = StorageNetwork.MODID, name = StorageNetwork.MODNAME, version = StorageNetwork.MODVERSION, dependencies = "required-after:limelib@[1.2.0,)")
public class StorageNetwork implements IGuiHandler {

	public static final String MODID = "storagenetwork";
	public static final String MODNAME = "StorageNetwork";
	public static final String MODVERSION = "@VERSION@";
	public static final Logger logger = LogManager.getLogger(MODNAME);

	@Instance(StorageNetwork.MODID)
	public static StorageNetwork instance;

	@SidedProxy(clientSide = "mrriegel.storagenetwork.proxy.ClientProxy", serverSide = "mrriegel.storagenetwork.proxy.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Start " + MODNAME + " version " + MODVERSION + (MODVERSION.equals("@VERSION@") ? ". Detected a development environment." : ""));
		ModConfig.refreshConfig(event.getSuggestedConfigurationFile());
		logger.info("[PreInitialize] Loading Blocks and Items");
		Registry.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, this);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

}
