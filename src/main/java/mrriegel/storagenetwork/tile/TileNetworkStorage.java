package mrriegel.storagenetwork.tile;

import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.Enums.IOMODE;
import mrriegel.storagenetwork.item.ItemItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileNetworkStorage extends TileNetworkItemConnection implements INetworkStorage<IItemHandler> {

	public IOMODE iomode = IOMODE.INOUT;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		iomode = IOMODE.values()[compound.getInteger("iomode")];
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("iomode", iomode.ordinal());
		return super.writeToNBT(compound);
	}

	@Override
	public IItemHandler getStorage() {
		return getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
	}

	@Override
	public GlobalBlockPos getStoragePosition() {
		return new GlobalBlockPos(pos.offset(tileFace), worldObj);
	}

	@Override
	public boolean canInsert() {
		return iomode.canInsert();
	}

	@Override
	public boolean canExtract() {
		return iomode.canExtract();
	}

	@Override
	public boolean canTransferItem(ItemStack stack) {
		return ItemItemFilter.canTransferItem(filter, stack);
	}

}
