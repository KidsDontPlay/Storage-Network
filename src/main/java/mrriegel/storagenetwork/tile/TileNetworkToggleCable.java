package mrriegel.storagenetwork.tile;


public class TileNetworkToggleCable extends TileNetworkCable implements IToggleable {

	@Override
	public boolean isActive() {
		return !worldObj.isBlockPowered(pos);
	}

}
