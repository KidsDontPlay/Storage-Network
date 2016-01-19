package mrriegel.cworks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTab {
	public static CreativeTabs tab1 = new CreativeTabs(CableWorks.MODID) {

		@Override
		public Item getTabIconItem() {
			return Items.gold_nugget;
		}

		@Override
		public String getTranslatedTabLabel() {
			return CableWorks.MODNAME;
		}
	};
}
