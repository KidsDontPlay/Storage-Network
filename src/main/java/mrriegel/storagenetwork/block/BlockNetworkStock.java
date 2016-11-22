package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkStock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkStock extends BlockNetworkConnection {

	public BlockNetworkStock() {
		super("block_network_stock");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkStock();
	}

	@Override
	protected Class<? extends TileNetworkConnection> getTile() {
		return TileNetworkStock.class;
	}

}
