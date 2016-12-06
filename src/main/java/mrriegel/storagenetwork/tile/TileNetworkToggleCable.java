package mrriegel.storagenetwork.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileNetworkToggleCable extends TileNetworkCable implements IToggleable {

	public boolean inverted = false;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inverted = compound.getBoolean("inverted");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("inverted", inverted);
		return super.writeToNBT(compound);
	}

	@Override
	public boolean isActive() {
		if (!inverted)
			return !worldObj.isBlockPowered(pos);
		else
			return worldObj.isBlockPowered(pos);
	}

}
