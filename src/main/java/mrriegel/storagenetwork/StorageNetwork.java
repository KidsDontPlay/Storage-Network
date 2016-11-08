package mrriegel.storagenetwork;

import mrriegel.storagenetwork.proxy.CommonProxy;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
		if(event.getSide().isClient()){
			logger.info("[PreInitialize] Registering Renderer");
			Registry.preInitClient();
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void place(NeighborNotifyEvent event) {
		if (event.getWorld().getTileEntity(event.getPos()) instanceof INetworkPart) {
			int tiles = 0;
			INetworkPart part = null;
			for (EnumFacing face : event.getNotifiedSides()) {
				BlockPos neighbor = event.getPos().offset(face);
				if (event.getWorld().getTileEntity(neighbor) != null) {
					tiles++;
					if (event.getWorld().getTileEntity(neighbor) instanceof INetworkPart)
						part = (INetworkPart) event.getWorld().getTileEntity(neighbor);
				}
			}
			if (tiles == 1 && part != null && part.getNetworkCore() != null) {
				part.getNetworkCore().network.addPart((INetworkPart) event.getWorld().getTileEntity(event.getPos()));
				return;
			}
		}
		for (EnumFacing face : event.getNotifiedSides()) {
			BlockPos neighbor = event.getPos().offset(face);
			TileEntity tile = event.getWorld().getTileEntity(neighbor);
			if (tile instanceof INetworkPart && ((INetworkPart) tile).getNetworkCore() != null)
				((INetworkPart) tile).getNetworkCore().markForNetworkInit();
			else if (tile instanceof TileNetworkCore)
				((TileNetworkCore) tile).markForNetworkInit();
		}
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
