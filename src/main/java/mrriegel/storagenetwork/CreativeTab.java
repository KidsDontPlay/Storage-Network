package mrriegel.storagenetwork;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTab extends CreativeTabs {

	public static final CreativeTab TAB = new CreativeTab();

	public CreativeTab() {
		super(StorageNetwork.MODID);
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(Registry.networkCore);
	}

	@Override
	public String getTranslatedTabLabel() {
		return StorageNetwork.MODNAME;
	}

}
