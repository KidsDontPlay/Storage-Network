package mrriegel.storagenetwork;

import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTab {
	public static CreativeTabs tab1 = new CreativeTabs(StorageNetwork.MODID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(ModBlocks.request);
		}

		@Override
		public String getTranslatedTabLabel() {
			return StorageNetwork.MODNAME;
		}
	};
}
