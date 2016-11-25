package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkCable;
import mrriegel.storagenetwork.tile.TileNetworkToggleCable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNetworkToggleCable extends BlockNetworkCable {

	public BlockNetworkToggleCable() {
		super("block_network_toggle_cable");
	}

	@Override
	protected Class<? extends TileNetworkCable> getTile() {
		return TileNetworkToggleCable.class;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkToggleCable();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		((TileNetworkToggleCable) worldIn.getTileEntity(pos)).markForSync();
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		TileNetworkToggleCable tile = (TileNetworkToggleCable) worldIn.getTileEntity(pos);
		//		tile.markForSync();
		if (!worldIn.isRemote && tile.getNetworkCore() != null) {
			tile.getNetworkCore().markForNetworkInit();
			BlockNetworkCable.releaseNetworkParts(worldIn, pos, tile.getNetworkCore().getPos());
		}
		super.neighborChanged(state, worldIn, pos, blockIn);
	}

}
