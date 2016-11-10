package mrriegel.storagenetwork.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileNetworkEnergyInterface extends TileNetworkConnection<IEnergyStorage> implements IEnergyReceiver, IEnergyProvider {

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
		if (getNetworkCore() != null)
			return getNetworkCore().energy.extractEnergy(maxExtract, simulate);
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (getNetworkCore() != null)
			return getNetworkCore().receiveEnergy(from,maxReceive, simulate);
		return 0;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && getNetworkCore() != null)
			return getNetworkCore().getCapability(capability, facing);
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && getNetworkCore() != null)
			return getNetworkCore().hasCapability(capability, facing);
		return super.hasCapability(capability, facing);
	}

}
