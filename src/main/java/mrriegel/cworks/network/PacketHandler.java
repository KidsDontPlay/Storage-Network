package mrriegel.cworks.network;

import mrriegel.cworks.CableWorks;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(
			CableWorks.MODID);

	public static void init() {
		int id = 0;
	}

}
