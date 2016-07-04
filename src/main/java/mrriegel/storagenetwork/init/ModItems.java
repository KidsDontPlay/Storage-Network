package mrriegel.storagenetwork.init;

import mrriegel.storagenetwork.items.ItemCoverStick;
import mrriegel.storagenetwork.items.ItemDuplicator;
import mrriegel.storagenetwork.items.ItemFRemote;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.items.ItemTemplate;
import mrriegel.storagenetwork.items.ItemUpgrade;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	public static final Item upgrade = new ItemUpgrade();
	public static final Item remote = new ItemRemote().setRegistryName("remote");
	public static final Item fremote = new ItemFRemote().setRegistryName("fremote");
	public static final Item coverstick = new ItemCoverStick();
	public static final Item template = new ItemTemplate();
	public static final Item duplicator = new ItemDuplicator();

	public static void init() {
		GameRegistry.register(upgrade);
		GameRegistry.register(remote.setUnlocalizedName(remote.getRegistryName().toString()));
		GameRegistry.register(fremote.setUnlocalizedName(fremote.getRegistryName().toString()));
		GameRegistry.register(coverstick);
		// GameRegistry.register(template);
		// GameRegistry.registerItem(ssdd);
		GameRegistry.register(duplicator);
	}

}
