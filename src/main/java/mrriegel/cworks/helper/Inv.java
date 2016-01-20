package mrriegel.cworks.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mrriegel.cworks.blocks.BlockKabel;
import mrriegel.cworks.init.ModBlocks;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileMaster;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Inv {
	public static int addToInventoriesWithLeftover(ItemStack stack,
			List<IInventory> inventorys, boolean simulate) {
		if (stack == null)
			return 0;
		ItemStack in = stack.copy();
		for (IInventory inv : getInventorys(inventorys, in, true)) {
			int remain = addToInventoryWithLeftover(in, inv, simulate);
			if (remain == 0)
				return 0;
			in = copyStack(in, remain);
			inv.markDirty();
		}
		for (IInventory inv : getInventorys(inventorys, in, false)) {
			int remain = addToInventoryWithLeftover(in, inv, simulate);
			if (remain == 0)
				return 0;
			in = copyStack(in, remain);
			inv.markDirty();
		}
		return in.stackSize;

	}

	public static int addToInventoriesWithLeftover(ItemStack stack,
			Map<IInventory, EnumFacing> inventorys, boolean simulate) {
		if (stack == null)
			return 0;
		ItemStack in = stack.copy();
		for (IInventory inv : getInventorys(inventorys, in, true)) {
			int remain = addToInventoryWithLeftover(in, inv, simulate);
			if (remain == 0)
				return 0;
			in = copyStack(in, remain);
			inv.markDirty();
		}
		for (IInventory inv : getInventorys(inventorys, in, false)) {
			int remain = addToInventoryWithLeftover(in, inv, simulate);
			if (remain == 0)
				return 0;
			in = copyStack(in, remain);
			inv.markDirty();
		}
		return in.stackSize;

	}

	private static EnumFacing getFace(ISidedInventory inv) {
		List<BlockPos> neighbors = new ArrayList<BlockPos>();
		TileEntity tile = (TileEntity) inv;
		for (BlockPos p : TileMaster.getSides(tile.getPos())) {
			if (tile.getWorld().getBlockState(p).getBlock() == ModBlocks.storageKabel)
				neighbors.add(p);
		}
		for (BlockPos n : neighbors) {
			EnumFacing f = BlockKabel.get(tile.getPos(), n);
			TileKabel tk = tile.getWorld().getTileEntity(n);

		}

	}

	public static boolean isInventorySame(IInventory a, IInventory b) {
		if (!(a instanceof TileEntity) || !(b instanceof TileEntity))
			return false;
		TileEntity aa = (TileEntity) a;
		TileEntity bb = (TileEntity) b;
		return aa.getPos().equals(bb.getPos());
	}

	static List<IInventory> getInventorys(
			Map<IInventory, EnumFacing> inventorys, ItemStack stack,
			boolean with) {
		List<IInventory> lis = new ArrayList<IInventory>();
		for (Entry<IInventory, EnumFacing> e : inventorys.entrySet()) {
			IInventory inv = e.getKey();
			if (with) {
				if (contains(inv, stack, e.getValue()))
					lis.add(inv);
			} else {
				if (!contains(inv, stack, e.getValue()))
					lis.add(inv);
			}
		}
		return lis;
	}

	static List<IInventory> getInventorys(List<IInventory> inventorys,
			ItemStack stack, boolean with) {
		List<IInventory> lis = new ArrayList<IInventory>();
		for (IInventory inv : inventorys) {
			if (with) {
				if (contains(inv, stack))
					lis.add(inv);
			} else {
				if (!contains(inv, stack))
					lis.add(inv);
			}
		}
		return lis;
	}

	static boolean contains(IInventory inv, ItemStack stack, EnumFacing face) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null
					&& inv.getStackInSlot(i).isItemEqual(stack)
					&& ItemStack.areItemStackTagsEqual(stack,
							inv.getStackInSlot(i))) {
				return true;
			}
		}
		return false;
	}

	static boolean contains(IInventory inv, ItemStack stack) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null
					&& inv.getStackInSlot(i).isItemEqual(stack)
					&& ItemStack.areItemStackTagsEqual(stack,
							inv.getStackInSlot(i))) {
				return true;
			}
		}
		return false;
	}

	/** nicked from reika */
	public static int addToInventoryWithLeftover(ItemStack stack,
			IInventory inventory, boolean simulate) {
		int left = stack.stackSize;
		int max = Math.min(inventory.getInventoryStackLimit(),
				stack.getMaxStackSize());
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (!inventory.isItemValidForSlot(i, stack))
				continue;
			if (in != null && stack.isItemEqual(in)
					&& ItemStack.areItemStackTagsEqual(stack, in)) {
				int space = max - in.stackSize;
				int add = Math.min(space, stack.stackSize);
				if (add > 0) {
					if (!simulate)
						in.stackSize += add;
					left -= add;
					if (left <= 0)
						return 0;
				}
			}
		}
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (!inventory.isItemValidForSlot(i, stack))
				continue;
			if (in == null) {
				int add = Math.min(max, left);
				if (!simulate)
					inventory
							.setInventorySlotContents(i, copyStack(stack, add));
				left -= add;
				if (left <= 0)
					return 0;
			}
		}
		return left;
	}

	/** nicked from reika */
	public static int addToSidedInventoryWithLeftover(ItemStack stack,
			ISidedInventory inventory, EnumFacing side, boolean simulate) {
		int left = stack.stackSize;
		int max = Math.min(inventory.getInventoryStackLimit(),
				stack.getMaxStackSize());
		for (int i : inventory.getSlotsForFace(side)) {
			ItemStack in = inventory.getStackInSlot(i);
			if (!inventory.isItemValidForSlot(i, stack)
					|| !inventory.canInsertItem(i, stack, side))
				continue;
			if (in != null && stack.isItemEqual(in)
					&& ItemStack.areItemStackTagsEqual(stack, in)) {
				int space = max - in.stackSize;
				int add = Math.min(space, stack.stackSize);
				if (add > 0) {
					if (!simulate)
						in.stackSize += add;
					left -= add;
					if (left <= 0)
						return 0;
				}
			}
		}
		for (int i : inventory.getSlotsForFace(side)) {
			ItemStack in = inventory.getStackInSlot(i);
			if (!inventory.isItemValidForSlot(i, stack)
					|| !inventory.canInsertItem(i, stack, side))
				continue;
			if (in == null) {
				int add = Math.min(max, left);
				if (!simulate)
					inventory
							.setInventorySlotContents(i, copyStack(stack, add));
				left -= add;
				if (left <= 0)
					return 0;
			}
		}
		return left;
	}

	public static ItemStack copyStack(ItemStack stack, int size) {
		if (stack == null)
			return null;
		ItemStack tmp = stack.copy();
		tmp.stackSize = size;
		return tmp;
	}
}
