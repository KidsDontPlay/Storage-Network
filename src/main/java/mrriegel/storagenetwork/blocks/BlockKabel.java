package mrriegel.storagenetwork.blocks;

import java.util.List;

import javax.annotation.Nullable;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileContainer;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKabel extends BlockConnectable {
	public static enum Connect {
		CONNECT, STORAGE, NULL
	}

	public BlockKabel() {
		super(Material.IRON);
		this.setHardness(1.4F);
		this.setCreativeTab(CreativeTab.tab1);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		if (worldIn.isRemote)
			return true;
		if (/* tile.getMaster() == null || */(heldItem != null && (heldItem.getItem() == ModItems.coverstick || heldItem.getItem() == ModItems.toggler || heldItem.getItem() == ModItems.duplicator)))
			return false;
		else if (tile.getKind() == Kind.exKabel || tile.getKind() == Kind.imKabel || tile.getKind() == Kind.storageKabel) {
			playerIn.openGui(StorageNetwork.instance, GuiHandler.CABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		super.neighborChanged(state, worldIn, pos, blockIn);
		worldIn.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));

	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		setConnections(worldIn, pos, state, false);
		worldIn.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}

	boolean validInventory(World worldIn, BlockPos pos) {
		return worldIn.getTileEntity(pos) instanceof IInventory;
	}

	@Override
	public void setConnections(World worldIn, BlockPos pos, IBlockState state, boolean refresh) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		EnumFacing face = null;
		BlockPos con = null;
		tile.north = getConnect(worldIn, pos, pos.north());
		tile.south = getConnect(worldIn, pos, pos.south());
		tile.east = getConnect(worldIn, pos, pos.east());
		tile.west = getConnect(worldIn, pos, pos.west());
		tile.down = getConnect(worldIn, pos, pos.down());
		tile.up = getConnect(worldIn, pos, pos.up());
		if (tile.north == Connect.STORAGE && validInventory(worldIn, pos.north())) {
			face = EnumFacing.NORTH;
			con = pos.north();
		} else if (tile.south == Connect.STORAGE && validInventory(worldIn, pos.south())) {
			face = EnumFacing.SOUTH;
			con = pos.south();
		} else if (tile.east == Connect.STORAGE && validInventory(worldIn, pos.east())) {
			face = EnumFacing.EAST;
			con = pos.east();
		} else if (tile.west == Connect.STORAGE && validInventory(worldIn, pos.west())) {
			face = EnumFacing.WEST;
			con = pos.west();
		} else if (tile.down == Connect.STORAGE && validInventory(worldIn, pos.down())) {
			face = EnumFacing.DOWN;
			con = pos.down();
		} else if (tile.up == Connect.STORAGE && validInventory(worldIn, pos.up())) {
			face = EnumFacing.UP;
			con = pos.up();
		}
		tile.setInventoryFace(face);
		tile.setConnectedInventory(con);
		super.setConnections(worldIn, pos, state, refresh);
		if (refresh)
			Util.updateTile(worldIn, pos);
	}

	boolean isConnectedToInventory(IBlockAccess world, BlockPos orig, BlockPos pos) {
		IBlockState s = world.getBlockState(orig);
		for (BlockPos p : Util.getSides(orig)) {
			if (p.equals(pos))
				continue;
			if (world.getTileEntity(p) instanceof TileContainer)
				continue;
			if (world.getTileEntity(p) instanceof ISidedInventory && (((ISidedInventory) world.getTileEntity(p)).getSlotsForFace(get(orig, p)).length != 0))
				return true;
			if (world.getTileEntity(p) instanceof IInventory)
				return true;
		}
		return false;
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
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		if (tile != null && tile.getCover() != null) {
			if (tile.getCover() != Blocks.GLASS)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
			else if (ConfigHandler.untouchable)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
			return;
		}
		float f = 0.3125F;
		float f1 = 0.6875F;
		float f2 = 0.3125F;
		float f3 = 0.6875F;
		float f4 = 0.3125F;
		float f5 = 0.6875F;
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));

		if (tile.north != Connect.NULL) {
			f2 = 0f;
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));
		}
		if (tile.south != Connect.NULL) {
			f3 = 1f;
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));
		}
		if (tile.west != Connect.NULL) {
			f = 0f;
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));
		}
		if (tile.east != Connect.NULL) {
			f1 = 1f;
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));
		}
		if (tile.down != Connect.NULL) {
			f4 = 0f;
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));
		}
		if (tile.up != Connect.NULL) {
			f5 = 1f;
			addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(f, f4, f2, f1, f5, f3));
		}

	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileKabel tile = (TileKabel) source.getTileEntity(pos);
		float f = 0.3125F;
		float f1 = 0.6875F;
		float f2 = 0.3125F;
		float f3 = 0.6875F;
		float f4 = 0.3125F;
		float f5 = 0.6875F;
		if (tile == null)
			return new AxisAlignedBB(f, f4, f2, f1, f5, f3);
		if (tile != null && tile.getCover() != null && tile.getCover() != Blocks.GLASS) {
			return FULL_BLOCK_AABB;
		}
		AxisAlignedBB res = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

		if (tile.north != Connect.NULL) {
			f2 = 0f;
		}
		if (tile.south != Connect.NULL) {
			f3 = 1f;
		}
		if (tile.west != Connect.NULL) {
			f = 0f;
		}
		if (tile.east != Connect.NULL) {
			f1 = 1f;
		}
		if (tile.down != Connect.NULL) {
			f4 = 0f;
		}
		if (tile.up != Connect.NULL) {
			f5 = 1f;
		}
		return new AxisAlignedBB(f, f4, f2, f1, f5, f3);
	}

	protected Connect getConnect(IBlockAccess worldIn, BlockPos orig, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		Block ori = worldIn.getBlockState(orig).getBlock();
		if (worldIn.getTileEntity(pos) instanceof IConnectable || worldIn.getTileEntity(pos) instanceof TileMaster)
			return Connect.CONNECT;
		if (ori == ModBlocks.kabel || ori == ModBlocks.vacuumKabel)
			return Connect.NULL;
		boolean inventory = worldIn.getTileEntity(pos) instanceof IInventory && !(worldIn.getTileEntity(pos) instanceof ISidedInventory);
		EnumFacing face = get(orig, pos);

		boolean sided = worldIn.getTileEntity(pos) instanceof ISidedInventory && (((ISidedInventory) worldIn.getTileEntity(pos)).getSlotsForFace(face).length != 0);
		if (!inventory && !sided)
			return Connect.NULL;
		if (isConnectedToInventory(worldIn, orig, pos))
			return Connect.NULL;
		return Connect.STORAGE;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileKabel) {
			TileKabel tile = (TileKabel) tileentity;
			for (int i = 0; i < tile.getUpgrades().size(); i++) {
				Util.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getUpgrades().get(i));
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileKabel();
	}

	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.exKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.kabel_E"));
			else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.imKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.kabel_I"));
			else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.storageKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.kabel_S"));
			else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.vacuumKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.kabel_V"));
			else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.kabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.kabel_L"));
			tooltip.add(I18n.format("tooltip.storagenetwork.networkNeeded"));
		}

	}

}
