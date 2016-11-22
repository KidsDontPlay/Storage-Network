package mrriegel.storagenetwork.tile;

import mrriegel.limelib.tile.IDataKeeper;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.Enums.IOMODE;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileBox<T, S> extends TileNetworkPart implements INetworkStorage<T, S>, IPriority, IDataKeeper {

	public IOMODE iomode = IOMODE.INOUT;
	public ItemStack filter;
	protected int priority;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		iomode = IOMODE.values()[compound.getInteger("iomode")];
		if (compound.hasKey("filter"))
			filter = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("filter"));
		priority = compound.getInteger("priority");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("iomode", iomode.ordinal());
		if (filter != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			filter.writeToNBT(nbt);
			compound.setTag("filter", nbt);
		}
		compound.setInteger("priority", priority);
		return super.writeToNBT(compound);
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(StorageNetwork.instance, GuiID.BOX.ordinal(), worldObj, getX(), getY(), getZ());
		return true;
	}

	@Override
	public GlobalBlockPos getStoragePosition() {
		return new GlobalBlockPos(pos, worldObj);
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
	public int getPriority() {
		return priority;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		switch (nbt.getInteger("buttonID")) {
		case 0:
			priority = Math.max(priority - (nbt.getBoolean("shift") ? 10 : 1), -99);
			break;
		case 1:
			priority = Math.min(priority + (nbt.getBoolean("shift") ? 10 : 1), 99);
			break;
		case 2:
			iomode = iomode.next();
			break;
		default:
			break;
		}
		markDirty();
	}

}
