package mrriegel.storagenetwork.tile;

import mrriegel.limelib.util.GlobalBlockPos;

/**
 * @author canitzp
 */
public interface INetworkPart {

    GlobalBlockPos getPosition();

    default int getInventorySpace(){
        return 0;
    }

}
