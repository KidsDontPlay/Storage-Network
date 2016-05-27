package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileCrafter extends CrunchTEInventory implements ISidedInventory, ITickable {
	int progress, duration;
	BlockPos master;

	public TileCrafter() {
		super(10);
		duration = 150;
	}

	@Override
	protected void readSyncableDataFromNBT(NBTTagCompound tag) {
		progress = tag.getInteger("progress");
		master = tag.getLong("master") == Long.MAX_VALUE ? null : BlockPos.fromLong(tag.getLong("master"));
	}

	@Override
	protected void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setInteger("progress", progress);
		if (master != null)
			tag.setLong("master", master.toLong());
		else
			tag.setLong("master", Long.MAX_VALUE);
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public BlockPos getMaster() {
		return master;
	}

	public void setMaster(BlockPos master) {
		this.master = master;
	}

}
