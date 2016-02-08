package mrriegel.storagenetwork.render;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandlers {

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		Object kabel = event.modelRegistry.getObject(CableModel.kabel);
		if (kabel != null) {
			event.modelRegistry.putObject(CableModel.kabel, new CableModel());
		}
		Object ex = event.modelRegistry.getObject(CableModel.ex);
		if (ex != null) {
			event.modelRegistry.putObject(CableModel.ex, new CableModel());
		}
		Object im = event.modelRegistry.getObject(CableModel.im);
		if (im != null) {
			event.modelRegistry.putObject(CableModel.im, new CableModel());
		}
		Object storage = event.modelRegistry.getObject(CableModel.storage);
		if (storage != null) {
			event.modelRegistry.putObject(CableModel.storage, new CableModel());
		}
		Object vacuum = event.modelRegistry.getObject(CableModel.vacuum);
		if (vacuum != null) {
			event.modelRegistry.putObject(CableModel.vacuum, new CableModel());
		}
	}

}
