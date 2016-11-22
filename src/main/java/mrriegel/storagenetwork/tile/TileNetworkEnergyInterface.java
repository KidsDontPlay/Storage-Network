package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.Enums.IOMODE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileNetworkEnergyInterface extends TileNetworkConnection implements IEnergyReceiver, IEnergyProvider {

	public IOMODE iomode = IOMODE.INOUT;

	@Override
	public int getEnergyStored(EnumFacing from) {
		if (getNetworkCore() != null)
			return getNetworkCore().getEnergyStored(from);
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if (getNetworkCore() != null)
			return getNetworkCore().getMaxEnergyStored(from);
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return getNetworkCore() != null;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (getNetworkCore() != null && iomode.canExtract() && (from == null || from.equals(tileFace)))
			return getNetworkCore().extractor.extractEnergy(maxExtract, simulate);
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (getNetworkCore() != null && iomode.canInsert() && (from == null || from.equals(tileFace)))
			return getNetworkCore().receiveEnergy(from, maxReceive, simulate);
		return 0;
	}

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
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && getNetworkCore() != null && (facing == null || facing.equals(tileFace))) {
			if (iomode.canExtract() && iomode.canInsert())
				return getNetworkCore().getCapability(capability, facing);
			else if (iomode.canExtract())
				return (T) getNetworkCore().extractor;
			else if (iomode.canInsert())
				return (T) getNetworkCore().receiver;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && getNetworkCore() != null && (facing == null || facing.equals(tileFace)))
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		iomode = iomode.next();
		for (EnumFacing f : EnumFacing.VALUES)
			worldObj.notifyBlockOfStateChange(pos.offset(f), worldObj.getBlockState(pos.offset(f)).getBlock());
		markDirty();
	}

}
