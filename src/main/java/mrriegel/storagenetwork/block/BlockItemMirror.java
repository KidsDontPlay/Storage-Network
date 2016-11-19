package mrriegel.storagenetwork.block;

import static net.minecraft.block.BlockHorizontal.FACING;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileItemMirror;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemMirror extends CommonBlockContainer<TileItemMirror> {

	public BlockItemMirror() {
		super(Material.IRON, "block_item_mirror");
		setHardness(2.5F);
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileItemMirror();
	}

	@Override
	protected Class<? extends TileItemMirror> getTile() {
		return TileItemMirror.class;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		((TileItemMirror) worldIn.getTileEntity(pos)).face = state.getValue(FACING);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		int h = 1000;
		switch (state.getValue(FACING)) {
		case EAST:
			if (hitY > .5f && hitZ > .5f)
				h = 0;
			if (hitY > .5f && hitZ < .5f)
				h = 1;
			if (hitY < .5f && hitZ > .5f)
				h = 2;
			if (hitY < .5f && hitZ < .5f)
				h = 3;
			break;
		case NORTH:
			if (hitX > .5 && hitY > .5f)
				h = 0;
			if (hitX < .5f && hitY > .5f)
				h = 1;
			if (hitX > .5f && hitY < .5f)
				h = 2;
			if (hitX < .5f && hitY < .5f)
				h = 3;
			break;
		case SOUTH:
			if (hitX < .5f && hitY > .5f)
				h = 0;
			if (hitX > .5 && hitY > .5f)
				h = 1;
			if (hitX < .5f && hitY < .5f)
				h = 2;
			if (hitX > .5f && hitY < .5f)
				h = 3;
			break;
		case WEST:
			if (hitY > .5f && hitZ < .5f)
				h = 0;
			if (hitY > .5f && hitZ > .5f)
				h = 1;
			if (hitY < .5f && hitZ < .5f)
				h = 2;
			if (hitY < .5f && hitZ > .5f)
				h = 3;
			break;
		default:
			break;
		}

		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}
	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		super.onBlockClicked(worldIn, pos, playerIn);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		boolean x = super.rotateBlock(world, pos, axis);
		onBlockAdded(world, pos, world.getBlockState(pos));
		return x;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

}
