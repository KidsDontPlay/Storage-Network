package mrriegel.cworks.blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mrriegel.cworks.CreativeTab;
import mrriegel.cworks.init.ModBlocks;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKabel extends BlockContainer {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool TOP = PropertyBool.create("top");
	public static final PropertyBool BOTTOM = PropertyBool.create("bottom");

	public BlockKabel() {
		super(Material.iron);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false))
				.withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false))
				.withProperty(TOP, Boolean.valueOf(false))
				.withProperty(BOTTOM, Boolean.valueOf(false)));
		this.setCreativeTab(CreativeTab.tab1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos,
			EnumFacing side) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos,
			IBlockState state, Block neighborBlock) {
		if (neighborBlock instanceof BlockKabel
				|| neighborBlock instanceof BlockRequest
				|| neighborBlock instanceof BlockMaster) {
			state = getActualState(state, worldIn, pos);
			setConnections(worldIn, pos, state);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		state = getActualState(state, worldIn, pos);
		setConnections(worldIn, pos, state);
	}

	private void setConnections(World worldIn, BlockPos pos, IBlockState state) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		Set<EnumFacing> set = new HashSet<EnumFacing>();
		if (state.getValue(NORTH))
			set.add(EnumFacing.NORTH);
		if (state.getValue(SOUTH))
			set.add(EnumFacing.SOUTH);
		if (state.getValue(EAST))
			set.add(EnumFacing.EAST);
		if (state.getValue(WEST))
			set.add(EnumFacing.WEST);
		if (state.getValue(BOTTOM))
			set.add(EnumFacing.DOWN);
		if (state.getValue(TOP))
			set.add(EnumFacing.UP);
		tile.setConnections(set);
		tile.setMaster(null);
		for (BlockPos n : TileMaster.getSides(pos)) {
			if (tile.getMaster() != null)
				break;
			if (worldIn.getTileEntity(n) instanceof TileMaster)
				tile.setMaster(worldIn.getTileEntity(n).getPos());
			if (worldIn.getTileEntity(n) instanceof TileKabel
					&& ((TileKabel) worldIn.getTileEntity(n)).getMaster() != null)
				tile.setMaster(((TileKabel) worldIn.getTileEntity(n))
						.getMaster());
		}
		if (tile.getMaster() == null
				|| worldIn.getTileEntity(tile.getMaster()) == null
				|| !(worldIn.getTileEntity(tile.getMaster()) instanceof TileMaster))
			setAllMastersNull(worldIn, pos);
		if (tile.getMaster() != null)
			((TileMaster) worldIn.getTileEntity(tile.getMaster()))
					.refreshNetwork();
	}

	private void setAllMastersNull(World world, BlockPos pos) {
		((TileKabel) world.getTileEntity(pos)).setMaster(null);
		for (BlockPos bl : TileMaster.getSides(pos)) {
			if (world.getBlockState(bl).getBlock() instanceof BlockKabel
					&& world.getChunkFromBlockCoords(bl).isLoaded()
					&& ((TileKabel) world.getTileEntity(bl)).getMaster() != null) {
				((TileKabel) world.getTileEntity(bl)).setMaster(null);
				setAllMastersNull(world, bl);
			}
		}
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos orig,
			BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		Block ori = worldIn.getBlockState(orig).getBlock();
		if (block == ModBlocks.master || block instanceof BlockKabel
				|| block == ModBlocks.request)
			return true;
		if (ori == ModBlocks.kabel || ori == ModBlocks.vacuumKabel)
			return false;
		boolean inventory = worldIn.getTileEntity(pos) instanceof IInventory
				&& !(worldIn.getTileEntity(pos) instanceof ISidedInventory);
		EnumFacing face = get(orig, pos);
		boolean sided = worldIn.getTileEntity(pos) instanceof ISidedInventory
				&& (((ISidedInventory) worldIn.getTileEntity(pos))
						.getSlotsForFace(face).length != 0);
		if (!inventory && !sided)
			return false;
		return inventory || sided;
	}

	boolean isConnectedToInventory(IBlockAccess world, BlockPos orig,
			BlockPos pos) {
		IBlockState s = world.getBlockState(orig);
		for (BlockPos p : TileMaster.getSides(orig)) {
			if (p.equals(pos))
				continue;
			if (world.getTileEntity(p) instanceof ISidedInventory
					&& (((ISidedInventory) world.getTileEntity(p))
							.getSlotsForFace(get(orig, p)).length != 0))
				return true;
			if (world.getTileEntity(p) instanceof IInventory)
				return true;
		}
		return false;
	}

	PropertyBool face2prop(EnumFacing face) {
		if (face == EnumFacing.DOWN)
			return BOTTOM;
		if (face == EnumFacing.UP)
			return TOP;
		if (face == EnumFacing.NORTH)
			return NORTH;
		if (face == EnumFacing.SOUTH)
			return SOUTH;
		if (face == EnumFacing.EAST)
			return EAST;
		if (face == EnumFacing.WEST)
			return WEST;
		return null;
	}

	private EnumFacing get(BlockPos a, BlockPos b) {
		if (a.up().equals(b))
			return EnumFacing.DOWN;
		if (a.down().equals(b))
			return EnumFacing.UP;
		if (a.west().equals(b))
			return EnumFacing.EAST;
		if (a.east().equals(b))
			return EnumFacing.WEST;
		if (a.north().equals(b))
			return EnumFacing.SOUTH;
		if (a.south().equals(b))
			return EnumFacing.NORTH;
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		boolean d = this.canConnectTo(worldIn, pos, pos.down());
		boolean u = this.canConnectTo(worldIn, pos, pos.up());
		boolean e = this.canConnectTo(worldIn, pos, pos.east());
		boolean w = this.canConnectTo(worldIn, pos, pos.west());
		boolean s = this.canConnectTo(worldIn, pos, pos.south());
		boolean n = this.canConnectTo(worldIn, pos, pos.north());
		
		float f = 0.3125F;
		float f1 = 0.6875F;
		float f2 = 0.3125F;
		float f3 = 0.6875F;
		float f4 = 0.3125F;
		float f5 = 0.6875F;

		if (n)
			f2 = 0.0F;
		if (s)
			f3 = 1.0F;
		if (w)
			f = 0.0F;
		if (e)
			f1 = 1.0F;
		if (d)
			f4 = 0.0f;
		if (u)
			f5 = 1.0f;
		this.setBlockBounds(f, f4, f2, f1, f5, f3);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos,
			IBlockState state) {
		setBlockBoundsBasedOnState(worldIn, pos);
		return super.getCollisionBoundingBox(worldIn, pos, state);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn,
			BlockPos pos) {
		List o = new ArrayList();
		boolean d = this.canConnectTo(worldIn, pos, pos.down());
		boolean u = this.canConnectTo(worldIn, pos, pos.up());
		boolean e = this.canConnectTo(worldIn, pos, pos.east());
		boolean w = this.canConnectTo(worldIn, pos, pos.west());
		boolean s = this.canConnectTo(worldIn, pos, pos.south());
		boolean n = this.canConnectTo(worldIn, pos, pos.north());
		return state.withProperty(NORTH, n).withProperty(EAST, e)
				.withProperty(SOUTH, s).withProperty(WEST, w)
				.withProperty(TOP, u).withProperty(BOTTOM, d);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { NORTH, EAST, WEST, SOUTH,
				TOP, BOTTOM });
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileKabel(worldIn, this);
	}

}
