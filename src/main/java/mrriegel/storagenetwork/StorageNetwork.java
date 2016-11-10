package mrriegel.storagenetwork;

import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkTunnel;
import mrriegel.storagenetwork.proxy.CommonProxy;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
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
			IBlockColor color = new IBlockColor() {
				@Override
				public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
					if (worldIn != null && pos != null && worldIn.getTileEntity(pos) != null) {
						return ((BlockNetworkTunnel) state.getBlock()).getMode().color;
					}
					return 0;
				}
			};
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(color, Registry.networkTunnelE);
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(color, Registry.networkTunnelI);
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(color, Registry.networkTunnelF);
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(color, Registry.networkTunnelR);
			IItemColor item = new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					if (stack != null)
						return ((BlockNetworkTunnel) Block.getBlockFromItem(stack.getItem())).getMode().color;
					return 0;
				}
			};
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(item, Registry.networkTunnelE);
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(item, Registry.networkTunnelI);
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(item, Registry.networkTunnelF);
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(item, Registry.networkTunnelR);
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
						if (((INetworkPart) event.getWorld().getTileEntity(neighbor)).getNeighborFaces().contains(face.getOpposite()))
							if (((INetworkPart) event.getWorld().getTileEntity(neighbor)).getNetworkCore() != null) {
								if (core == null) {
									core = ((INetworkPart) event.getWorld().getTileEntity(neighbor)).getNetworkCore();
								} else {
									if (!core.getPos().equals(((INetworkPart) event.getWorld().getTileEntity(neighbor)).getNetworkCore().getPos()))
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
