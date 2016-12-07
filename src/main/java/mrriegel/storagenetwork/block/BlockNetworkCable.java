package mrriegel.storagenetwork.block;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.Enums.Connect;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCable;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkToggleCable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

public class BlockNetworkCable extends CommonBlockContainer<CommonTile> {

	public static final IProperty<Connect> NORTH = PropertyEnum.<Connect> create("north", Connect.class);
	public static final IProperty<Connect> SOUTH = PropertyEnum.<Connect> create("south", Connect.class);
	public static final IProperty<Connect> WEST = PropertyEnum.<Connect> create("west", Connect.class);
	public static final IProperty<Connect> EAST = PropertyEnum.<Connect> create("east", Connect.class);
	public static final IProperty<Connect> UP = PropertyEnum.<Connect> create("up", Connect.class);
	public static final IProperty<Connect> DOWN = PropertyEnum.<Connect> create("down", Connect.class);

	public static final BiMap<EnumFacing, IProperty<Connect>> bimap = HashBiMap.<EnumFacing, IProperty<Connect>> create(6);
	static {
		bimap.put(EnumFacing.NORTH, NORTH);
		bimap.put(EnumFacing.SOUTH, SOUTH);
		bimap.put(EnumFacing.WEST, WEST);
		bimap.put(EnumFacing.EAST, EAST);
		bimap.put(EnumFacing.UP, UP);
		bimap.put(EnumFacing.DOWN, DOWN);
	}

	public BlockNetworkCable(String name) {
		super(Material.IRON, name);
		setHardness(0.3F);
		setCreativeTab(CreativeTab.TAB);
		setDefaultState(getDefaultState().withProperty(NORTH, Connect.NULL).withProperty(SOUTH, Connect.NULL).withProperty(WEST, Connect.NULL).withProperty(EAST, Connect.NULL).withProperty(UP, Connect.NULL).withProperty(DOWN, Connect.NULL));
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkCable();
	}

	@Override
	protected Class<? extends TileNetworkCable> getTile() {
		return TileNetworkCable.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, SOUTH, WEST, EAST, UP, DOWN);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		worldIn.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(NORTH, getConnect(worldIn, pos, EnumFacing.NORTH)).withProperty(SOUTH, getConnect(worldIn, pos, EnumFacing.SOUTH)).withProperty(WEST, getConnect(worldIn, pos, EnumFacing.WEST)).withProperty(EAST, getConnect(worldIn, pos, EnumFacing.EAST)).withProperty(UP, getConnect(worldIn, pos, EnumFacing.UP)).withProperty(DOWN, getConnect(worldIn, pos, EnumFacing.DOWN));
	}

	public Connect getConnect(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileNetworkCable tile = (TileNetworkCable) worldIn.getTileEntity(pos);
		if (tile == null || !tile.getValidSides().get(facing))
			return Connect.NULL;
		TileEntity tileSide = worldIn.getTileEntity(pos.offset(facing));
		if (isNetworkPart(tileSide) && tile.isSideValid(facing) && tileSide != null && (!(tileSide instanceof TileNetworkCable) || ((TileNetworkCable) tileSide).isSideValid(facing.getOpposite())))
			return Connect.CABLE;
		if (tile instanceof TileNetworkToggleCable && worldIn.getBlockState(pos.offset(facing)).canProvidePower())
			return Connect.CABLE;
		return Connect.NULL;
	}

	private boolean isNetworkPart(TileEntity tile) {
		return tile instanceof INetworkPart || tile instanceof TileNetworkCore;
	}

	public EnumFacing getFace(float hitX, float hitY, float hitZ, IBlockState state) {
		if (!center(hitY) && !center(hitZ))
			if (hitX < .25F)
				return EnumFacing.WEST;
			else if (hitX > .75F)
				return EnumFacing.EAST;
		if (!center(hitY) && !center(hitX))
			if (hitZ < .25F)
				return EnumFacing.NORTH;
			else if (hitZ > .75F)
				return EnumFacing.SOUTH;
		if (!center(hitX) && !center(hitZ))
			if (hitY < .25F)
				return EnumFacing.DOWN;
			else if (hitY > .75F)
				return EnumFacing.UP;
		return null;
	}

	private boolean center(float foo) {
		return foo > .25f && foo < .25f;
	}

	protected double getStart() {
		return 6. / 16.;
	}

	protected double getEnd() {
		return 1. - getStart();
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		state = getActualState(state, worldIn, pos);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(getStart(), getStart(), getStart(), getEnd(), getEnd(), getEnd()));
		if (state.getValue(DOWN) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(getStart(), 0.0, getStart(), getEnd(), getStart(), getEnd()));
		if (state.getValue(UP) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(getStart(), getEnd(), getStart(), getEnd(), 1, getEnd()));
		if (state.getValue(WEST) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0, getStart(), getStart(), getStart(), getEnd(), getEnd()));
		if (state.getValue(EAST) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(getEnd(), getStart(), getStart(), 1, getEnd(), getEnd()));
		if (state.getValue(NORTH) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(getStart(), getStart(), 0, getEnd(), getEnd(), getStart()));
		if (state.getValue(SOUTH) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(getStart(), getStart(), getEnd(), getEnd(), getEnd(), 1));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = getActualState(state, source, pos);
		double f = getStart();
		double f1 = getEnd();
		double f2 = getStart();
		double f3 = getEnd();
		double f4 = getStart();
		double f5 = getEnd();
		if (state.getValue(NORTH) != Connect.NULL)
			f2 = 0;
		if (state.getValue(SOUTH) != Connect.NULL)
			f3 = 1;
		if (state.getValue(WEST) != Connect.NULL)
			f = 0;
		if (state.getValue(EAST) != Connect.NULL)
			f1 = 1;
		if (state.getValue(DOWN) != Connect.NULL)
			f4 = 0;
		if (state.getValue(UP) != Connect.NULL)
			f5 = 1;
		return new AxisAlignedBB(f, f4, f2, f1, f5, f3);
	}

	public static void releaseNetworkParts(World world, BlockPos pos, final BlockPos core) {
		if (core == null || !(world.getTileEntity(core) instanceof TileNetworkCore)) {
			TileEntity current = world.getTileEntity(pos);
			if (current instanceof INetworkPart && ((INetworkPart) current).getNetworkCore() != null && ((INetworkPart) current).getNetworkCore().getPos().equals(core)) {
				INetworkPart part = (INetworkPart) current;
				part.setNetworkCore(null);
			}
			Set<EnumFacing> f = (world.getTileEntity(pos) instanceof INetworkPart) ? ((INetworkPart) world.getTileEntity(pos)).getNeighborFaces() : Sets.newHashSet(EnumFacing.VALUES);
			for (EnumFacing face : f) {
				BlockPos nei = pos.offset(face);
				if (world.getTileEntity(nei) instanceof INetworkPart) {
					INetworkPart part = (INetworkPart) world.getTileEntity(nei);
					if (part.getNetworkCore() != null) {
						releaseNetworkParts(world, nei, core);
					}
				}
			}
		} else {
			TileNetworkCore c = (TileNetworkCore) world.getTileEntity(core);
			if (c != null && c.network != null)
				for (INetworkPart part : c.network.networkParts) {
					part.setNetworkCore(null);
				}
		}
	}
}
