package mrriegel.storagenetwork.tile;

import mrriegel.limelib.util.GlobalBlockPos;

/**
 * @author canitzp
 */
public interface INetworkPart {
	
    GlobalBlockPos getPosition();
    
    TileNetworkCore getNetworkCore();
    
    void setNetworkCore(TileNetworkCore core);

    default int getInventorySpace(){
        return 0;
    }

}
