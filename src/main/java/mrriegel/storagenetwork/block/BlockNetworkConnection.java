package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.Enums.Connect;
import mrriegel.storagenetwork.tile.TileNetworkConnection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockNetworkConnection extends BlockNetworkCable {

	public BlockNetworkConnection(String name) {
		super(name);
	}

	@Override
	public abstract TileEntity createTileEntity(World world, IBlockState state);

	@Override
	protected abstract Class<? extends TileNetworkConnection> getTile();

	protected EnumFacing getTileFace(IBlockAccess world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof TileNetworkConnection)
			return ((TileNetworkConnection) world.getTileEntity(pos)).tileFace;
		return EnumFacing.DOWN;
	}

	@Override
	public Connect getConnect(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		if (facing == getTileFace(worldIn, pos))
			return Connect.TILE;
		return super.getConnect(worldIn, pos, facing);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(map.get(facing.getOpposite()), Connect.TILE);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		for (EnumFacing face : EnumFacing.VALUES)
			if (state.getValue(map.get(face)) == Connect.TILE) {
				((TileNetworkConnection) worldIn.getTileEntity(pos)).tileFace = face;
				break;
			}
	}

	@Override
	protected EnumFacing getFace(float hitX, float hitY, float hitZ, IBlockState state) {
		EnumFacing sup = super.getFace(hitX, hitY, hitZ, state);
		EnumFacing tile = null;
		for (EnumFacing face : EnumFacing.VALUES)
			if (state.getValue(map.get(face)) == Connect.TILE) {
				tile = face;
				break;
			}
		if (sup == tile)
			return null;
		return sup;
	}

	@Override
	protected double getStart() {
		return 4./16.;
	}


}
