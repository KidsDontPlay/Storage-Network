package mrriegel.storagenetwork.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileToggler extends TileConnectable {

	private boolean disabled;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		disabled = compound.getBoolean("more");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("more", disabled);
		return compound;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
