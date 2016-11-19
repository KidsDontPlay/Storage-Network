package mrriegel.storagenetwork;

import mrriegel.limelib.network.PacketHandler;
import mrriegel.storagenetwork.block.BlockItemMirror;
import mrriegel.storagenetwork.container.ContainerAbstractRequest;
import mrriegel.storagenetwork.message.MessageCoreSync;
import mrriegel.storagenetwork.message.MessageInvTweaks;
import mrriegel.storagenetwork.message.MessageItemFilter;
import mrriegel.storagenetwork.message.MessageItemListRequest;
import mrriegel.storagenetwork.message.MessageRequest;
import mrriegel.storagenetwork.proxy.ClientProxy;
import mrriegel.storagenetwork.proxy.CommonProxy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
@Mod(modid = StorageNetwork.MODID, name = StorageNetwork.MODNAME, version = StorageNetwork.MODVERSION, dependencies = "required-after:limelib@[1.3.0,)")
public class StorageNetwork {

	public static final String MODID = "storagenetwork";
	public static final String MODNAME = "Storage Network";
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
		logger.info("[PreInitialize] Registering Blocks and Items");
		Registry.preInit();
		if (event.getSide().isClient()) {
			logger.info("[PreInitialize] Registering Renderer");
			Registry.preInitClient();
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(Network.class);
		MinecraftForge.EVENT_BUS.register(BlockItemMirror.class);
		logger.info("[Initialize] Registering Messages");
		PacketHandler.registerMessage(MessageItemFilter.class, Side.SERVER);
		PacketHandler.registerMessage(MessageItemListRequest.class, Side.CLIENT);
		PacketHandler.registerMessage(MessageRequest.class, Side.SERVER);
		PacketHandler.registerMessage(MessageCoreSync.class, Side.CLIENT);
		PacketHandler.registerMessage(MessageInvTweaks.class, Side.SERVER);

		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setString("ContainerClass", ContainerAbstractRequest.class.getName());
		tagCompound.setBoolean("PhantomItems", false);
		tagCompound.setInteger("GridSlotNumber", 1);
		tagCompound.setInteger("ButtonOffsetX", 62);
		tagCompound.setInteger("ButtonOffsetY", 161);
		FMLInterModComms.sendMessage("craftingtweaks", "RegisterProvider", tagCompound);
		if (event.getSide().isClient()) {
			ClientProxy.init();
		}
	}

}
