package mrriegel.storagenetwork.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKabel extends BlockContainer {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("top");
	public static final PropertyBool DOWN = PropertyBool.create("bottom");

	public BlockKabel() {
		super(Material.iron);
		this.setHardness(1.4F);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false))
				.withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false))
				.withProperty(UP, Boolean.valueOf(false))
				.withProperty(DOWN, Boolean.valueOf(false)));
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
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		worldIn.markBlockForUpdate(pos);
		if (playerIn.getHeldItem() != null
				&& playerIn.getHeldItem().getItem() == ModItems.upgrade
				&& !playerIn.isSneaking()
				&& (tile.getKind() == Kind.imKabel || tile.getKind() == Kind.exKabel)) {
			switch (playerIn.getHeldItem().getItemDamage()) {
			case 0:
				if (tile.elements(0) < 4) {
					tile.getDeque().push(0);
					playerIn.getHeldItem().stackSize--;
					if (playerIn.getHeldItem().stackSize <= 0)
						playerIn.inventory.setInventorySlotContents(
								playerIn.inventory.currentItem, null);
				}
				break;
			case 1:
				if (tile.elements(1) < 1) {
					tile.getDeque().push(1);
					playerIn.getHeldItem().stackSize--;
					if (playerIn.getHeldItem().stackSize <= 0)
						playerIn.inventory.setInventorySlotContents(
								playerIn.inventory.currentItem, null);
				}
				break;

			default:
				break;
			}
		} else if (playerIn.getHeldItem() == null
				&& playerIn.isSneaking()
				&& (tile.getKind() == Kind.imKabel || tile.getKind() == Kind.exKabel)) {
			if (!tile.getDeque().isEmpty()) {
				if (playerIn.inventory.addItemStackToInventory(new ItemStack(
						ModItems.upgrade, 1, tile.getDeque().peekFirst()))) {
					tile.getDeque().pollFirst();
				}

			}
		} else
			switch (tile.getKind()) {
			case exKabel:
				playerIn.openGui(StorageNetwork.instance, GuiHandler.CABLE,
						worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			case imKabel:
				playerIn.openGui(StorageNetwork.instance, GuiHandler.CABLE,
						worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			case storageKabel:
				playerIn.openGui(StorageNetwork.instance, GuiHandler.CABLE,
						worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			default:
				break;
			}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side,
				hitX, hitY, hitZ);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos,
			IBlockState state, Block neighborBlock) {
		state = getActualState(state, worldIn, pos);
		setConnections(worldIn, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		state = getActualState(state, worldIn, pos);
		setConnections(worldIn, pos, state);
	}

	public static void setConnections(World worldIn, BlockPos pos,
			IBlockState state) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		EnumFacing face = null;
		BlockPos con = null;
		if (state.getValue(NORTH)
				&& worldIn.getTileEntity(pos.north()) instanceof IInventory) {
			face = EnumFacing.NORTH;
			con = pos.north();
		} else if (state.getValue(SOUTH)
				&& worldIn.getTileEntity(pos.south()) instanceof IInventory) {
			face = EnumFacing.SOUTH;
			con = pos.south();
		} else if (state.getValue(EAST)
				&& worldIn.getTileEntity(pos.east()) instanceof IInventory) {
			face = EnumFacing.EAST;
			con = pos.east();
		} else if (state.getValue(WEST)
				&& worldIn.getTileEntity(pos.west()) instanceof IInventory) {
			face = EnumFacing.WEST;
			con = pos.west();
		} else if (state.getValue(DOWN)
				&& worldIn.getTileEntity(pos.down()) instanceof IInventory) {
			face = EnumFacing.DOWN;
			con = pos.down();
		} else if (state.getValue(UP)
				&& worldIn.getTileEntity(pos.up()) instanceof IInventory) {
			face = EnumFacing.UP;
			con = pos.up();
		}
		tile.setInventoryFace(face);
		tile.setConnectedInventory(con);
		if (tile.getMaster() == null) {
			for (BlockPos p : TileMaster.getSides(pos)) {
				if (worldIn.getTileEntity(p) instanceof TileMaster) {
					tile.setMaster(p);
				}
			}
		}
		if (tile.getMaster() != null) {
			TileEntity mas = worldIn.getTileEntity(tile.getMaster());

			tile.setMaster(null);
			worldIn.markBlockForUpdate(pos);
			setAllMastersNull(worldIn, pos);
			if (mas instanceof TileMaster) {
				((TileMaster) mas).refreshNetwork();
			}

		}

	}

	public static void setAllMastersNull(World world, BlockPos pos) {
		((IConnectable) world.getTileEntity(pos)).setMaster(null);
		for (BlockPos bl : TileMaster.getSides(pos)) {
			if (world.getTileEntity(bl) instanceof IConnectable
					&& world.getChunkFromBlockCoords(bl).isLoaded()
					&& ((IConnectable) world.getTileEntity(bl)).getMaster() != null) {
				((IConnectable) world.getTileEntity(bl)).setMaster(null);
				world.markBlockForUpdate(bl);
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
		if (isConnectedToInventory(worldIn, orig, pos))
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
			return DOWN;
		if (face == EnumFacing.UP)
			return UP;
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

	public static EnumFacing get(BlockPos a, BlockPos b) {
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
		boolean dNew = this.canConnectTo(worldIn, pos, pos.down());
		boolean uNew = this.canConnectTo(worldIn, pos, pos.up());
		boolean eNew = this.canConnectTo(worldIn, pos, pos.east());
		boolean wNew = this.canConnectTo(worldIn, pos, pos.west());
		boolean sNew = this.canConnectTo(worldIn, pos, pos.south());
		boolean nNew = this.canConnectTo(worldIn, pos, pos.north());
		// boolean dOld = state.getValue(DOWN);
		// boolean uOld = state.getValue(UP);
		// boolean eOld = state.getValue(EAST);
		// boolean wOld = state.getValue(WEST);
		// boolean sOld = state.getValue(SOUTH);
		// boolean nOld = state.getValue(NORTH);

		return state.withProperty(NORTH, nNew).withProperty(EAST, eNew)
				.withProperty(SOUTH, sNew).withProperty(WEST, wNew)
				.withProperty(UP, uNew).withProperty(DOWN, dNew);
	}

	List<BlockPos> getInventories(IBlockAccess worldIn,
			Map<BlockPos, Boolean> map) {
		List<BlockPos> lis = new ArrayList<BlockPos>();
		for (Entry<BlockPos, Boolean> e : map.entrySet()) {
			if (e.getValue()
					&& worldIn.getTileEntity(e.getKey()) instanceof IInventory)
				lis.add(e.getKey());

		}
		return lis;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileKabel) {
			TileKabel tile = (TileKabel) tileentity;
			while (!tile.getDeque().isEmpty()) {
				BlockRequest.spawnItemStack(worldIn, pos.getX(), pos.getY(),
						pos.getZ(), new ItemStack(ModItems.upgrade, 1, tile
								.getDeque().pollFirst()));
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { NORTH, EAST, WEST, SOUTH,
				UP, DOWN });
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileKabel(worldIn, this);
	}

}
