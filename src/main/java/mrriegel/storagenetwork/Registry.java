package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkCore;
import mrriegel.storagenetwork.block.BlockNetworkEnergyCell;
import mrriegel.storagenetwork.block.BlockNetworkEnergyInterface;
import mrriegel.storagenetwork.block.BlockNetworkExporter;
import mrriegel.storagenetwork.block.BlockNetworkImporter;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author canitzp
 */
public class Registry {

	public static final CommonBlock networkCore = new BlockNetworkCore();
	public static final CommonBlock networkCable = new BlockNetworkCable("block_network_cable");
//	public static final CommonBlock networkTunnelE=new BlockNetworkTunnel("block_network_tunnel_"+Mode.ENERGY.name().toLowerCase());
//	public static final CommonBlock networkTunnelI=new BlockNetworkTunnel("block_network_tunnel_"+Mode.ITEM.name().toLowerCase());
//	public static final CommonBlock networkTunnelF = new BlockNetworkTunnel("block_network_tunnel_" + Mode.FLUID.name().toLowerCase());
//	public static final CommonBlock networkTunnelR = new BlockNetworkTunnel("block_network_tunnel_" + Mode.REDSTONE.name().toLowerCase());
	public static final CommonBlock networkExporter = new BlockNetworkExporter();
	public static final CommonBlock networkImporter = new BlockNetworkImporter();
	public static final CommonBlock networkEnergyInterface = new BlockNetworkEnergyInterface();
	public static final CommonBlock networkEnergyCell = new BlockNetworkEnergyCell();
	
	public static void preInit() {
		networkCore.registerBlock();
		networkCable.registerBlock();
//		networkTunnelE.registerBlock();
//		networkTunnelI.registerBlock();
//		networkTunnelF.registerBlock();
//		networkTunnelR.registerBlock();
		networkExporter.registerBlock();
		networkImporter.registerBlock();
		networkEnergyInterface.registerBlock();
		networkEnergyCell.registerBlock();
		
		initRecipes();
	}

	public static void preInitClient() {
		networkCore.initModel();
		networkCable.initModel();
//		networkTunnelE.initModel();
//		networkTunnelI.initModel();
//		networkTunnelF.initModel();
//		networkTunnelR.initModel();
		networkExporter.initModel();
		networkImporter.initModel();
		networkEnergyInterface.initModel();
		networkEnergyCell.initModel();

	}

	private static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(networkCable, 8), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);
	}

}
