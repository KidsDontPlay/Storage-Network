package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.config.ConfigHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileItemBox extends AbstractFilterTile {

	private ItemStackHandler inv = new ItemStackHandler(ConfigHandler.itemBoxCapacity);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readInventory(compound);
	}

	public void readInventory(NBTTagCompound compound) {
		NBTTagList invList = compound.getTagList("box", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSlots()) {
				inv.setStackInSlot(slot, ItemStack.loadItemStackFromNBT(stackTag));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeInventory(compound);
		return compound;
	}

	public void writeInventory(NBTTagCompound compound) {
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		compound.setTag("box", invList);
	}

	@Override
	public IFluidHandler getFluidTank() {
		return null;
	}

	@Override
	public IItemHandler getInventory() {
		return inv;
	}

	@Override
	public BlockPos getSource() {
		return pos;
	}

	@Override
	public boolean isFluid() {
		return false;
	}

	@Override
	public boolean isStorage() {
		return true;
	}

}
