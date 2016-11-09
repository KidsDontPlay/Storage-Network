package mrriegel.storagenetwork.tile;

import java.util.EnumSet;

import net.minecraft.util.EnumFacing;
import mrriegel.limelib.util.GlobalBlockPos;

/**
 * @author canitzp
 */
public interface INetworkPart {
	
    GlobalBlockPos getPosition();
    
    TileNetworkCore getNetworkCore();
    
    void setNetworkCore(TileNetworkCore core);
    
	default EnumSet<EnumFacing> getNeighborFaces() {
		return EnumSet.allOf(EnumFacing.class);}

    default int getInventorySpace(){
        return 0;
    }

}
