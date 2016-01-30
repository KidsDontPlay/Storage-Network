package mrriegel.storagenetwork.gui.cable;

import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryFilter implements IInventory {
	protected int INVSIZE;
	protected ItemStack[] inv;
	TileKabel tile;

	public InventoryFilter(TileKabel tile, int size) {
		INVSIZE = size;
		inv = new ItemStack[INVSIZE];
		this.tile = tile;
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		if (size == 9) {
			NBTTagList invList = nbt.getTagList("crunchTE",
					Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < invList.tagCount(); i++) {
				NBTTagCompound stackTag = invList.getCompoundTagAt(i);
				int slot = stackTag.getByte("Slot");
				setInventorySlotContents(slot,
						ItemStack.loadItemStackFromNBT(stackTag));
			}
		} else if (size == 1) {
			if (nbt.hasKey("stack", 10))
				setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(nbt
						.getCompoundTag("stack")));
			else
				setInventorySlotContents(0, null);
		}
	}

	public ItemStack[] getInv() {
		return inv;
	}

	@Override
	public void clear() {
		inv = new ItemStack[INVSIZE];
	}

	@Override
	public int getSizeInventory() {
		return INVSIZE;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if (this.inv[slot] != null) {
			ItemStack itemstack;
			if (this.inv[slot].stackSize <= size) {
				itemstack = this.inv[slot];
				this.inv[slot] = null;
				return itemstack;
			} else {
				itemstack = this.inv[slot].splitStack(size);

				if (this.inv[slot].stackSize == 0) {
					this.inv[slot] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.inv[slot] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public String getName() {
		return tile.getWorld().getBlockState(tile.getPos()).getBlock()
				.getLocalizedName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack d = getStackInSlot(index);
		if (d == null)
			return null;
		setInventorySlotContents(index, null);
		return d.copy();
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

}