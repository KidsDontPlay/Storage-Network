package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkStorage extends BlockNetworkConnection {

	public BlockNetworkStorage() {
		super("block_network_storage");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkStorage();
	}

	@Override
	protected Class<? extends TileNetworkConnection> getTile() {
		return TileNetworkStorage.class;
	}

}
