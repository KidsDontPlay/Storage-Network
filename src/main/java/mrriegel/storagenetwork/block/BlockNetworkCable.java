package mrriegel.storagenetwork.block;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Maps;

public class BlockNetworkCable extends CommonBlockContainer<TileNetworkCable> {

	public static final IProperty<Connect> NORTH = PropertyEnum.<Connect> create("north", Connect.class);
	public static final IProperty<Connect> SOUTH = PropertyEnum.<Connect> create("south", Connect.class);
	public static final IProperty<Connect> WEST = PropertyEnum.<Connect> create("west", Connect.class);
	public static final IProperty<Connect> EAST = PropertyEnum.<Connect> create("east", Connect.class);
	public static final IProperty<Connect> UP = PropertyEnum.<Connect> create("up", Connect.class);
	public static final IProperty<Connect> DOWN = PropertyEnum.<Connect> create("down", Connect.class);

	public static enum Connect implements IStringSerializable {
		NULL("null"), CABLE("cable"), TILE("tile");
		String name;

		private Connect(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	public BlockNetworkCable() {
		super(Material.IRON, "block_network_cable");
		setHardness(1.0F);
		setCreativeTab(CreativeTab.TAB);
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

	//	@Override
	//	public BlockRenderLayer getBlockLayer() {
	//		return BlockRenderLayer.CUTOUT;
	//	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
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
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(NORTH, getConnect(worldIn, pos, EnumFacing.NORTH)).withProperty(SOUTH, getConnect(worldIn, pos, EnumFacing.SOUTH)).withProperty(WEST, getConnect(worldIn, pos, EnumFacing.WEST)).withProperty(EAST, getConnect(worldIn, pos, EnumFacing.EAST)).withProperty(UP, getConnect(worldIn, pos, EnumFacing.UP)).withProperty(DOWN, getConnect(worldIn, pos, EnumFacing.DOWN));
	}

	public Connect getConnect(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileNetworkCable tile = (TileNetworkCable) worldIn.getTileEntity(pos);
		if (tile != null && !tile.getValids().get(facing))
			return Connect.NULL;
		if (isNetworkPart(worldIn.getTileEntity(pos.offset(facing))) && /*TODO maybe?*/tile.getValids().get(facing) && ((TileNetworkCable) worldIn.getTileEntity(pos.offset(facing))).getValids().get(facing.getOpposite()))
			return Connect.CABLE;
		else if (/*TODO maybe?*/tile.getValids().get(facing) && validTile(worldIn, pos.offset(facing), facing.getOpposite()))
			return Connect.TILE;
		return Connect.NULL;
	}

	private boolean isNetworkPart(TileEntity tile) {
		//TODO
		return tile instanceof INetworkPart;
	}

	protected boolean validTile(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		return InvHelper.hasItemHandler(worldIn, pos, facing);
	}

	private Map<EnumFacing, IProperty<Connect>> map = Maps.newHashMap();
	
	{
		map.put(EnumFacing.NORTH, NORTH);
		map.put(EnumFacing.SOUTH, SOUTH);
		map.put(EnumFacing.WEST, WEST);
		map.put(EnumFacing.EAST, EAST);
		map.put(EnumFacing.UP, UP);
		map.put(EnumFacing.DOWN, DOWN);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		EnumFacing f = getFace(hitX, hitY, hitZ);
		TileNetworkCable tile = (TileNetworkCable) worldIn.getTileEntity(pos);
		if (new ItemStack(Items.STICK).isItemEqual(playerIn.inventory.getCurrentItem())) {
			if (f != null) {
				tile.getValids().put(f, false);
				if (worldIn.getTileEntity(pos.offset(f)) instanceof TileNetworkCable) {
					((TileNetworkCable) worldIn.getTileEntity(pos.offset(f))).getValids().put(f.getOpposite(), false);
					((TileNetworkCable) worldIn.getTileEntity(pos.offset(f))).markForSync();
				}
				tile.markForSync();
			} else {
				if (state.getValue(map.get(side)) == Connect.NULL && !((TileNetworkCable) worldIn.getTileEntity(pos)).getValids().get(side)) {
					tile.getValids().put(side, true);
					if (worldIn.getTileEntity(pos.offset(side)) instanceof TileNetworkCable) {
						((TileNetworkCable) worldIn.getTileEntity(pos.offset(side))).getValids().put(side.getOpposite(), true);
						((TileNetworkCable) worldIn.getTileEntity(pos.offset(side))).markForSync();
					}
					tile.markForSync();
				}
			}
		} else
			//			System.out.println(tile.getValids())
			;
		worldIn.markBlockRangeForRenderUpdate(pos.add(1, 1, 1), pos.add(-1, -1, -1));
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	protected EnumFacing getFace(float hitX, float hitY, float hitZ) {
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

	private static final double start = 5. / 16., end = 1. - start;

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		state = getActualState(state, worldIn, pos);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(start, start, start, end, end, end));
		if (state.getValue(DOWN) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(start, 0.0, start, end, start, end));
		if (state.getValue(UP) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(start, end, start, end, 1, end));
		if (state.getValue(WEST) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0, start, start, start, end, end));
		if (state.getValue(EAST) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(end, start, start, 1, end, end));
		if (state.getValue(NORTH) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(start, start, 0, end, end, start));
		if (state.getValue(SOUTH) != Connect.NULL)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(start, start, end, end, end, 1));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = getActualState(state, source, pos);
		double f = start;
		double f1 = end;
		double f2 = start;
		double f3 = end;
		double f4 = start;
		double f5 = end;
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

}
