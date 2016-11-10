package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkConnection;
import mrriegel.storagenetwork.block.BlockNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkConnection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author canitzp
 */
public class Registry {

	public static final CommonBlock networkCore = new BlockNetworkCore();
	public static final CommonBlock networkCable = new BlockNetworkCable("block_network_cable");
	public static final CommonBlock networkConnection=new BlockNetworkConnection("block_network_connection") {
		
		@Override
		protected Class<? extends TileNetworkConnection> getTile() {
			return TileNetworkConnection.class;
		}
		
		@Override
		public TileEntity createTileEntity(World world, IBlockState state) {
			return new TileNetworkConnection();
		}
	};

	public static void preInit() {
		networkCore.registerBlock();
		networkCable.registerBlock();
		networkConnection.registerBlock();

		initRecipes();
	}

	public static void preInitClient() {
		networkCore.initModel();
		networkCable.initModel();
		networkConnection.initModel();

	}

	private static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(networkCable, 8), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);
	}

}
