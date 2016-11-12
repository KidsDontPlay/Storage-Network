package mrriegel.storagenetwork.tile;


public class TileNetworkToggleCable extends TileNetworkCable implements IToggleable {

//	private boolean powered;

	@Override
	public boolean isActive() {
		return !worldObj.isBlockPowered(pos);
	}

//	@Override
//	public void readFromNBT(NBTTagCompound compound) {
//		powered = compound.getBoolean("power");
//		super.readFromNBT(compound);
//	}
//
//	@Override
//	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
//		compound.setBoolean("power", powered);
//		return super.writeToNBT(compound);
//	}
//
//	public boolean isPowered() {
//		return powered;
//	}
//
//	public void setPowered(boolean powered) {
//		this.powered = powered;
//	}

}
