package mrriegel.storagenetwork;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
@Mod(modid = StorageNetwork.MODID, name = StorageNetwork.MODNAME, version = StorageNetwork.MODVERSION)
public class StorageNetwork {

    public static final String MODID = "storagenetwork";
    public static final String MODNAME = "StorageNetwork";
    public static final String MODVERSION = "@VERSION@";
    public static final Logger logger = LogManager.getLogger(MODNAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger.info("Start " + MODNAME + " version " + MODVERSION + (MODVERSION.equals("@VERSION@") ? ". Detected a development environment." : ""));
    }

}
