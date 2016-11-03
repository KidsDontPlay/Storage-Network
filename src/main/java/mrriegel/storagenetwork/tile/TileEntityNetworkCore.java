package mrriegel.storagenetwork.tile;

import mrriegel.limelib.tile.CommonTile;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.NetworkSave;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author canitzp
 */
public class TileEntityNetworkCore extends CommonTile {

    public Network network;

    public void setNetwork(Network network){
        if(network != null){
            this.network = network;
            NetworkSave.networks.add(network);
        } else {
            NetworkSave.removeNetwork(this.getPos());
        }
    }

    public void initializeNetwork(){
        Network network = new Network();
        network.corePosition = this.getPos();
        //TODO check cable, blocks around, ... for network blocks and add them to the network
        setNetwork(network);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.network = NetworkSave.getNetwork(this.getPos());
        super.readFromNBT(compound);
    }
}
