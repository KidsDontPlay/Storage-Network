package mrriegel.storagenetwork.tile;

import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.tile.IDataKeeper;
import mrriegel.limelib.util.EnergyStorageExt;
import mrriegel.storagenetwork.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileNetworkEnergyCell extends TileNetworkPart implements IDataKeeper {

	private EnergyStorageExt energy = new EnergyStorageExt(ModConfig.energycellCapacity, Integer.MAX_VALUE);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		energy.setEnergyStored(compound.getInteger("energy"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("energy", energy.getEnergyStored());
		return super.writeToNBT(compound);
	}

	public EnergyStorageExt getEnergy() {
		return energy;
	}

	@Override
	public void writeToStack(ItemStack stack) {
		NBTStackHelper.setInt(stack, "energy", energy.getEnergyStored());
	}

	@Override
	public void readFromStack(ItemStack stack) {
		energy.setEnergyStored(NBTStackHelper.getInt(stack, "energy"));
	}

}
