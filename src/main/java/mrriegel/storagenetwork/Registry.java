package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkCable.Connect;
import mrriegel.storagenetwork.block.BlockNetworkConnection;
import mrriegel.storagenetwork.block.BlockNetworkCore;
import mrriegel.storagenetwork.block.BlockNetworkTunnel;
import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkTunnel.Mode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author canitzp
 */
public class Registry {

	public static final CommonBlock networkCore = new BlockNetworkCore();
	public static final CommonBlock networkCable = new BlockNetworkCable("block_network_cable");
	public static final CommonBlock networkTunnelE=new BlockNetworkTunnel("block_network_tunnel_"+Mode.ENERGY.name().toLowerCase());
	public static final CommonBlock networkTunnelI=new BlockNetworkTunnel("block_network_tunnel_"+Mode.ITEM.name().toLowerCase());
	public static final CommonBlock networkTunnelF=new BlockNetworkTunnel("block_network_tunnel_"+Mode.FLUID.name().toLowerCase());
	public static final CommonBlock networkTunnelR=new BlockNetworkTunnel("block_network_tunnel_"+Mode.REDSTONE.name().toLowerCase());
	
	public static void preInit() {
		networkCore.registerBlock();
		networkCable.registerBlock();
		networkTunnelE.registerBlock();
		networkTunnelI.registerBlock();
		networkTunnelF.registerBlock();
		networkTunnelR.registerBlock();

		initRecipes();
	}

	public static void preInitClient() {
		networkCore.initModel();
		networkCable.initModel();
		networkTunnelE.initModel();
		networkTunnelI.initModel();
		networkTunnelF.initModel();
		networkTunnelR.initModel();

		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
				ModelResourceLocation mrl=Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper().putAllStateModelLocations().get(iBlockState);
				if(iBlockState.getValue(BlockNetworkCable.DOWN)==Connect.CABLE)
					return new ModelResourceLocation(networkCore.getRegistryName(), "normal");
				return ModelCover.variantTag;
			}
		};
//		ModelLoader.setCustomStateMapper(Registry.networkCable, ignoreState);
	}

	private static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(networkCable, 8), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);
	}

}
