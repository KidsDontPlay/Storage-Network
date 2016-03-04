package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.blocks.PropertyConnection.Connect;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.IFluidHandler;

public class BlockFKabel extends BlockKabel {
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		onNeighborBlockChange(worldIn, pos, state, null);
		if (tile.getMaster() == null || (playerIn.getHeldItem() != null && playerIn.getHeldItem().getItem() == ModItems.coverstick))
			return false;
		if (playerIn.getHeldItem() != null && playerIn.getHeldItem().getItem() == ModItems.upgrade && !playerIn.isSneaking() && (tile.getKind() == Kind.fimKabel || tile.getKind() == Kind.fexKabel) && tile.getDeque() != null) {
			switch (playerIn.getHeldItem().getItemDamage()) {
			case ItemUpgrade.SPEED:
				if (tile.elements(ItemUpgrade.SPEED) < 4) {
					tile.getDeque().push(ItemUpgrade.SPEED);
					playerIn.getHeldItem().stackSize--;
					if (playerIn.getHeldItem().stackSize <= 0)
						playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
				}
				break;
			case ItemUpgrade.OP:
				if (tile.elements(ItemUpgrade.OP) < 1) {
					tile.getDeque().push(ItemUpgrade.OP);
					playerIn.getHeldItem().stackSize--;
					if (playerIn.getHeldItem().stackSize <= 0)
						playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
				}
				break;
			case ItemUpgrade.STOCK:
				if (tile.elements(ItemUpgrade.STOCK) < 1 && tile.getKind() == Kind.exKabel) {
					tile.getDeque().push(ItemUpgrade.STOCK);
					playerIn.getHeldItem().stackSize--;
					if (playerIn.getHeldItem().stackSize <= 0)
						playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
				}
				break;
			default:
				break;
			}
		} else if (playerIn.getHeldItem() == null && playerIn.isSneaking() && (tile.getKind() == Kind.fimKabel || tile.getKind() == Kind.fexKabel) && tile.getDeque() != null) {
			if (!tile.getDeque().isEmpty()) {
				if (playerIn.inventory.addItemStackToInventory(new ItemStack(ModItems.upgrade, 1, tile.getDeque().peekFirst()))) {
					tile.getDeque().pollFirst();
				}
			}
		} else
			switch (tile.getKind()) {
			case fexKabel:
				playerIn.openGui(StorageNetwork.instance, GuiHandler.FCABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			case fimKabel:
				playerIn.openGui(StorageNetwork.instance, GuiHandler.FCABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			case fstorageKabel:
				playerIn.openGui(StorageNetwork.instance, GuiHandler.FCABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			default:
				break;
			}
		playerIn.openContainer.detectAndSendChanges();
		return false;
	}

	@Override
	public void setConnections(World worldIn, BlockPos pos, IBlockState bState) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		EnumFacing face = null;
		BlockPos con = null;
		IExtendedBlockState state = (IExtendedBlockState) bState;
		if (state.getValue(NORTH) == Connect.STORAGE && worldIn.getTileEntity(pos.north()) instanceof IFluidHandler) {
			face = EnumFacing.NORTH;
			con = pos.north();
		} else if (state.getValue(SOUTH) == Connect.STORAGE && worldIn.getTileEntity(pos.south()) instanceof IFluidHandler) {
			face = EnumFacing.SOUTH;
			con = pos.south();
		} else if (state.getValue(EAST) == Connect.STORAGE && worldIn.getTileEntity(pos.east()) instanceof IFluidHandler) {
			face = EnumFacing.EAST;
			con = pos.east();
		} else if (state.getValue(WEST) == Connect.STORAGE && worldIn.getTileEntity(pos.west()) instanceof IFluidHandler) {
			face = EnumFacing.WEST;
			con = pos.west();
		} else if (state.getValue(DOWN) == Connect.STORAGE && worldIn.getTileEntity(pos.down()) instanceof IFluidHandler) {
			face = EnumFacing.DOWN;
			con = pos.down();
		} else if (state.getValue(UP) == Connect.STORAGE && worldIn.getTileEntity(pos.up()) instanceof IFluidHandler) {
			face = EnumFacing.UP;
			con = pos.up();
		}
		tile.setInventoryFace(face);
		tile.setConnectedInventory(con);
		if (tile.getMaster() == null) {
			for (BlockPos p : Util.getSides(pos)) {
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

	boolean isConnectedToFluidHandler(IBlockAccess world, BlockPos orig, BlockPos pos) {
		IBlockState s = world.getBlockState(orig);
		for (BlockPos p : Util.getSides(orig)) {
			if (p.equals(pos))
				continue;
			if (world.getTileEntity(p) instanceof IFluidHandler && (((IFluidHandler) world.getTileEntity(p)).getTankInfo(get(orig, p)).length != 0))
				return true;
		}
		return false;
	}

	@Override
	protected Connect getConnect(IBlockAccess worldIn, BlockPos orig, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		Block ori = worldIn.getBlockState(orig).getBlock();
		if (worldIn.getTileEntity(pos) instanceof IConnectable || worldIn.getTileEntity(pos) instanceof TileMaster)
			return Connect.CONNECT;
		if (ori == ModBlocks.kabel || ori == ModBlocks.vacuumKabel)
			return Connect.NULL;
		EnumFacing face = get(orig, pos);
		boolean sided = worldIn.getTileEntity(pos) instanceof IFluidHandler;
		if (!sided)
			return Connect.NULL;
		if (isConnectedToFluidHandler(worldIn, orig, pos))
			return Connect.NULL;
		return Connect.STORAGE;
	}
}
