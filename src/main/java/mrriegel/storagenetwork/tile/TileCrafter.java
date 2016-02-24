package mrriegel.storagenetwork.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileCrafter extends CrunchTEInventory implements ISidedInventory, ITickable {
	public static final int DURATION = 20;
	int progress;

	public TileCrafter() {
		super(10);
	}

	@Override
	protected void readSyncableDataFromNBT(NBTTagCompound tag) {
		progress = tag.getInteger("progress");
	}

	@Override
	protected void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setInteger("progress", progress);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index > 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 0;
	}

	private InventoryCrafting getMatrix() {
		InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer playerIn) {
				return false;
			}
		}, 3, 3);
		for (int i = 1; i < 10; i++)
			craftMatrix.setInventorySlotContents(i - 1, getStackInSlot(i));
		return craftMatrix;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) {
			return;
		}
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(getMatrix(), this.worldObj);
		if (result == null) {
			progress = 0;
			return;
		}
		if (canProcess()) {
			progress++;
			if (progress >= DURATION) {
				processItem();
				progress = 0;
			}
			worldObj.markBlockForUpdate(pos);
		}
	}

	private void processItem() {
		if (this.canProcess()) {
			ItemStack itemstack = CraftingManager.getInstance().findMatchingRecipe(getMatrix(), this.worldObj);

			if (getStackInSlot(0) == null) {
				setInventorySlotContents(0, itemstack.copy());
			} else if (getStackInSlot(0).isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(getStackInSlot(0), itemstack)) {
				getStackInSlot(0).stackSize += itemstack.stackSize;
			}
			for (int i = 1; i < 10; i++) {
				if (getStackInSlot(i) == null)
					continue;
				--this.getStackInSlot(i).stackSize;

				if (this.getStackInSlot(i).stackSize <= 0) {
					setInventorySlotContents(i, null);
				}
			}
			worldObj.markBlockForUpdate(pos);
		}
	}

	private boolean canProcess() {
		ItemStack itemstack = CraftingManager.getInstance().findMatchingRecipe(getMatrix(), this.worldObj);
		if (itemstack == null)
			return false;
		if (getStackInSlot(0) == null)
			return true;
		if (!getStackInSlot(0).isItemEqual(itemstack) || !ItemStack.areItemStackTagsEqual(getStackInSlot(0), itemstack))
			return false;
		int result = getStackInSlot(0).stackSize + itemstack.stackSize;
		return result <= getInventoryStackLimit() && result <= getStackInSlot(0).getMaxStackSize();
	}

	public boolean canInsert() {
		for (int i = 0; i < 10; i++) {
			if (getStackInSlot(i) != null)
				return false;
		}
		return true;
	}

}
