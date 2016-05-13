package mrriegel.storagenetwork.init;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.items.ItemCoverStick;
import mrriegel.storagenetwork.items.ItemFRemote;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.items.ItemSSDD;
import mrriegel.storagenetwork.items.ItemTemplate;
import mrriegel.storagenetwork.items.ItemToggler;
import mrriegel.storagenetwork.items.ItemUpgrade;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(value = StorageNetwork.MODID)
public class ModItems {
	public static final Item upgrade = new ItemUpgrade();
	public static final Item remote = new ItemRemote();
	public static final Item fremote = new ItemFRemote();
	public static final Item coverstick = new ItemCoverStick();
	public static final Item template = new ItemTemplate();
	public static final Item toggler = new ItemToggler();
	public static final Item ssdd = new ItemSSDD();

	public static void init() {
		GameRegistry.registerItem(upgrade, "upgrade");
		GameRegistry.registerItem(remote, "remote");
		GameRegistry.registerItem(fremote, "fremote");
		GameRegistry.registerItem(coverstick, "coverstick");
		// GameRegistry.registerItem(template, "template");
		GameRegistry.registerItem(toggler, "toggler");
		// GameRegistry.registerItem(ssdd, "ssdd");
	}

}
