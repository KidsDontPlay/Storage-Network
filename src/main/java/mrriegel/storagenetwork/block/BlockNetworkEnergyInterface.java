package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkEnergyInterface;
import mrriegel.storagenetwork.tile.TileNetworkExporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkEnergyInterface extends BlockNetworkConnection{

	public BlockNetworkEnergyInterface() {
		super("block_network_energy_interface");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkEnergyInterface();
	}

	@Override
	protected Class<? extends TileNetworkConnection> getTile() {
		return TileNetworkEnergyInterface.class;
	}

}
