package mrriegel.storagenetwork.init;

import mrriegel.storagenetwork.items.ItemCoverStick;
import mrriegel.storagenetwork.items.ItemDuplicator;
import mrriegel.storagenetwork.items.ItemFRemote;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.items.ItemSSDD;
import mrriegel.storagenetwork.items.ItemTemplate;
import mrriegel.storagenetwork.items.ItemToggler;
import mrriegel.storagenetwork.items.ItemUpgrade;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	public static final Item upgrade = new ItemUpgrade();
	public static final Item remote = new ItemRemote().setRegistryName("remote");
	public static final Item fremote = new ItemFRemote().setRegistryName("fremote");
	public static final Item coverstick = new ItemCoverStick();
	public static final Item template = new ItemTemplate();
	public static final Item toggler = new ItemToggler();
	public static final Item ssdd = new ItemSSDD();
	public static final Item duplicator = new ItemDuplicator();

	public static void init() {
		GameRegistry.registerItem(upgrade);
		GameRegistry.registerItem(remote.setUnlocalizedName(remote.getRegistryName()));
		GameRegistry.registerItem(fremote.setUnlocalizedName(fremote.getRegistryName()));
		GameRegistry.registerItem(coverstick);
		// GameRegistry.registerItem(template);
		GameRegistry.registerItem(toggler);
		// GameRegistry.registerItem(ssdd);
		GameRegistry.registerItem(duplicator);
	}

}
