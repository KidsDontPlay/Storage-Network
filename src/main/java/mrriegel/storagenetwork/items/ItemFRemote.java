package mrriegel.storagenetwork.items;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;

public class ItemFRemote extends ItemRemote {

	public ItemFRemote() {
		super();
		this.setUnlocalizedName(StorageNetwork.MODID + ":fremote");
	}

	@Override
	protected int getGui() {
		return GuiHandler.FREMOTE;
	}
}
