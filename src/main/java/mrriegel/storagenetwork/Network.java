package mrriegel.storagenetwork;

import java.util.Set;

import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;

import com.google.common.collect.Sets;

/**
 * @author canitzp
 */
public class Network {

	public GlobalBlockPos corePosition;
	public Set<INetworkPart> networkParts = Sets.newHashSet();

    public void addPart(INetworkPart part){
   		networkParts.add(part);
   		part.setNetworkCore((TileNetworkCore) corePosition.getTile(null));
    }
    
    public void removePart(INetworkPart part){
   		networkParts.remove(part);
   		part.setNetworkCore(null);
    }

    @Override
    public String toString() {
        return "Network at '" + corePosition.toString() + "'. Data: {" + networkParts.toString() + "}";
    }
    
    
}
