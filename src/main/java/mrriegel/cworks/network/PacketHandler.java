package mrriegel.cworks.network;

import mrriegel.cworks.CableWorks;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(
			CableWorks.MODID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(ButtonMessage.class, ButtonMessage.class,
				id++, Side.SERVER);
		INSTANCE.registerMessage(StacksMessage.class, StacksMessage.class,
				id++, Side.CLIENT);
		INSTANCE.registerMessage(RequestMessage.class, RequestMessage.class,
				id++, Side.SERVER);
	}

}
