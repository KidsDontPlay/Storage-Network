package mrriegel.storagenetwork;

import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.proxy.CommonProxy;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
@Mod(modid = StorageNetwork.MODID, name = StorageNetwork.MODNAME, version = StorageNetwork.MODVERSION, dependencies = "required-after:limelib@[1.2.0,)")
public class StorageNetwork {

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
		if (event.getSide().isClient()) {
			logger.info("[PreInitialize] Registering Renderer");
			Registry.preInitClient();
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(this);
		if (event.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(ModelCover.class);
		}
	}

	@SubscribeEvent
	public void place(NeighborNotifyEvent event) {
		if (!event.getWorld().isAirBlock(event.getPos()) && !event.getState().getBlock().hasTileEntity(event.getState()))
			return;
		if (event.getWorld().getTileEntity(event.getPos()) instanceof INetworkPart) {
			boolean invalid = false;
			TileNetworkCore core = null;
			for (EnumFacing face : event.getNotifiedSides()) {
				BlockPos neighbor = event.getPos().offset(face);
				if (event.getWorld().getTileEntity(neighbor) != null) {
					if (event.getWorld().getTileEntity(neighbor) instanceof INetworkPart) {
						INetworkPart part=(INetworkPart) event.getWorld().getTileEntity(neighbor);
						if (part.getNeighborFaces().contains(face.getOpposite()))
							if (part.getNetworkCore() != null) {
								if (core == null) {
									core = part.getNetworkCore();
								} else {
									if (!core.getPos().equals(part.getNetworkCore().getPos()))
										invalid = true;
								}
							} else
								invalid = true;
					} else
						invalid = true;
				}
			}
			if (!invalid && core != null) {
				core.network.addPart((INetworkPart) event.getWorld().getTileEntity(event.getPos()));
				return;
			}
		}
		for (EnumFacing face : event.getNotifiedSides()) {
			BlockPos neighbor = event.getPos().offset(face);
			TileEntity tile = event.getWorld().getTileEntity(neighbor);
			if (tile instanceof INetworkPart && ((INetworkPart) tile).getNetworkCore() != null && ((INetworkPart) tile).getNeighborFaces().contains(face.getOpposite())) {
				((INetworkPart) tile).getNetworkCore().markForNetworkInit();
				BlockNetworkCable.releaseNetworkParts(event.getWorld(), tile.getPos(), ((INetworkPart) tile).getNetworkCore().getPos());
			} else if (tile instanceof TileNetworkCore) {
				((TileNetworkCore) tile).markForNetworkInit();
				BlockNetworkCable.releaseNetworkParts(event.getWorld(), tile.getPos(), tile.getPos());
			}
		}
	}

}
