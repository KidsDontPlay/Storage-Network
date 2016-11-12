package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkEnergyInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNetworkEnergyInterface extends BlockNetworkConnection {

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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		player.openGui(StorageNetwork.instance, GuiID.ENERGY_INTERFACE.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

}
