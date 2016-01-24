package mrriegel.storagenetwork;

import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTab {
	public static CreativeTabs tab1 = new CreativeTabs(StorageNetwork.MODID) {

		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(ModBlocks.kabel);
		}

		@Override
		public String getTranslatedTabLabel() {
			return StorageNetwork.MODNAME;
		}
	};
}
