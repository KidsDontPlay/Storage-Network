package mrriegel.cworks.gui;

import mrriegel.cworks.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

public class CrunchItemInventory implements IInventory {
	protected final int INVSIZE;
	protected ItemStack[] inv;
	protected final int stackLimit;
	public ItemStack storedInv = null;

	public CrunchItemInventory(int size, int stackLimit, ItemStack stack) {
		this.INVSIZE = size;
		inv = new ItemStack[size];
		this.stackLimit = stackLimit;
		this.storedInv = stack;
		if (!storedInv.hasTagCompound()) {
			storedInv.setTagCompound(new NBTTagCompound());
		}
		if (storedInv != null && storedInv.getTagCompound() != null) {
			NBTTagList invList = storedInv.getTagCompound().getTagList(
					"crunchItem", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < invList.tagCount(); i++) {
				NBTTagCompound stackTag = invList.getCompoundTagAt(i);
				int slot = stackTag.getByte("Slot");
				if (slot >= 0 && slot < getInv().length) {
					setInventorySlotContents(slot,
							ItemStack.loadItemStackFromNBT(stackTag));
				}
			}
		}
	}

	public CrunchItemInventory(int size, ItemStack stack) {
		this(size, 64, stack);
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
		return this.stackLimit;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getHeldItem() != null
				&& player.getHeldItem().isItemEqual(storedInv);
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
		return storedInv.getDisplayName();
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
