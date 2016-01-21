package mrriegel.cworks;

import mrriegel.cworks.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CableWorks.MODID, name = CableWorks.MODNAME, version = CableWorks.VERSION, guiFactory = "mrriegel.cworks.config.GuiFactory")
public class CableWorks {
	public static final String MODID = "cworks";
	public static final String VERSION = "1.0";
	public static final String MODNAME = "Cable Works";

	@Instance(CableWorks.MODID)
	public static CableWorks instance;

	@SidedProxy(clientSide = "mrriegel.cworks.proxy.ClientProxy", serverSide = "mrriegel.cworks.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
