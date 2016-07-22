package mrriegel.storagenetwork.tile;

import javax.annotation.Nullable;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TileCrafter extends TileConnectable implements ISidedInventory, ITickable {
	int progress, duration;
	private ItemStack[] inv;
	public static final int SIZE = 10;

	public TileCrafter() {
		super();
		inv = new ItemStack[SIZE];
		duration = 150;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		progress = compound.getInteger("progress");

		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			if (j >= 0 && j < this.inv.length) {
				this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("progress", progress);

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inv.length; ++i) {
			if (this.inv[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		compound.setTag("Items", nbttaglist);
		return super.writeToNBT(compound);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getDuration() {
		return duration;
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

	public InventoryCrafting getMatrix() {
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
		boolean m = false;
		for (BlockPos p : Util.getSides(pos))
			if (worldObj.getBlockState(p).getBlock() == ModBlocks.container && ((IConnectable) worldObj.getTileEntity(p)).getMaster() != null) {
				master = ((IConnectable) worldObj.getTileEntity(p)).getMaster();
				break;
			}
		if (master == null)
			duration = 150;
		else
			duration = 20;
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(getMatrix(), this.worldObj);
		if (result == null) {
			progress = 0;
			return;
		}
		if (canProcess()) {
			if (master != null && worldObj.getTileEntity(master) instanceof TileMaster)
				if (!((TileMaster) worldObj.getTileEntity(master)).consumeRF(1, false))
					return;
			progress++;
			if (progress >= duration) {
				processItem();
				progress = 0;
			}
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

	@Override
	@Nullable
	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < this.inv.length ? this.inv[index] : null;
	}

	@Override
	@Nullable
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.inv, index, count);

		if (itemstack != null) {
			this.markDirty();
		}

		return itemstack;
	}

	@Override
	@Nullable
	public ItemStack removeStackFromSlot(int index) {
		if (this.inv[index] != null) {
			ItemStack itemstack = this.inv[index];
			this.inv[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		this.inv[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public String getName() {
		return "null";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]);
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
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
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
	public void clear() {
		inv = new ItemStack[SIZE];
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

}
