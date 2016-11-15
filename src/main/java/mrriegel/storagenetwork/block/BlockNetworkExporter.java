package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkExporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkExporter extends BlockNetworkConnection {

	public BlockNetworkExporter() {
		super("block_network_exporter");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkExporter();
	}

	@Override
	protected Class<? extends TileNetworkConnection> getTile() {
		return TileNetworkExporter.class;
	}

}
