package mrriegel.storagenetwork.tile;

import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.NetworkSave;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author canitzp
 */
public class TileEntityNetworkCore extends CommonTile {

	public Network network;

	public void setNetwork(Network network) {
		if (network != null) {
			this.network = network;
			NetworkSave.networks.add(network);
		} else {
			NetworkSave.removeNetwork(new GlobalBlockPos(pos, worldObj.provider.getDimension()));
		}
	}

	public void initializeNetwork() {
		Network network = new Network();
		network.corePosition = new GlobalBlockPos(pos, worldObj.provider.getDimension());
		//TODO check cable, blocks around, ... for network blocks and add them to the network
		setNetwork(network);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.network = NetworkSave.getNetwork(new GlobalBlockPos(pos, worldObj.provider.getDimension()));
		super.readFromNBT(compound);
	}
}
