package mrriegel.storagenetwork.item;

import mrriegel.limelib.item.CommonSubtypeItem;
import mrriegel.storagenetwork.CreativeTab;

public class ItemUpgrade extends CommonSubtypeItem {

	public ItemUpgrade() {
		super("item_upgrade", UpgradeType.values().length);
		setCreativeTab(CreativeTab.TAB);
	}

	public enum UpgradeType {
		SPEED, STACK;
	}
}
