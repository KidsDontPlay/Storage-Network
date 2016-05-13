package mrriegel.storagenetwork.network;

import mrriegel.storagenetwork.StorageNetwork;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(StorageNetwork.MODID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(ButtonMessage.class, ButtonMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(StacksMessage.class, StacksMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(FluidsMessage.class, FluidsMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(RequestMessage.class, RequestMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(FRequestMessage.class, FRequestMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ClearMessage.class, ClearMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(SortMessage.class, SortMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(RecipeMessage.class, RecipeMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(LimitMessage.class, LimitMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(InsertMessage.class, InsertMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(StackMessage.class, StackMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(FilterMessage.class, FilterMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(TemplateMessage.class, TemplateMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(FaceMessage.class, FaceMessage.class, id++, Side.SERVER);
	}

}
