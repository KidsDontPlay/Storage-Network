package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkImporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkImporter extends BlockNetworkConnection {

	public BlockNetworkImporter() {
		super("block_network_importer");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkImporter();
	}

	@Override
	protected Class<? extends TileNetworkConnection> getTile() {
		return TileNetworkImporter.class;
	}

}
