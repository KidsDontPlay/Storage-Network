package mrriegel.storagenetwork.tile;

import java.util.EnumSet;
import java.util.Set;

import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author canitzp
 */
public interface INetworkPart {
	
    GlobalBlockPos getPosition();
    
    TileNetworkCore getNetworkCore();
    
    void setNetworkCore(TileNetworkCore core);
    
	default Set<EnumFacing> getNeighborFaces() {
		return EnumSet.allOf(EnumFacing.class);}

    default int getInventorySpace(){
        return 0;
    }

}
