package mrriegel.storagenetwork;

import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.tile.INetworkPart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
public class Network {

	public GlobalBlockPos corePosition;
	public List<INetworkPart> networkParts = new ArrayList<>();

    public void addPart(INetworkPart part){
        networkParts.add(part);
    }

    @Override
    public String toString() {
        return "Network at '" + corePosition.toString() + "'. Data: {" + networkParts.toString() + "}";
    }
}
