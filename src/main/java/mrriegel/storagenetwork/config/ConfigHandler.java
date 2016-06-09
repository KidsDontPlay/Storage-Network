package mrriegel.storagenetwork.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class ConfigHandler {

	public static Configuration config;

	public static boolean smallFont, energyNeeded, untouchable, jeiLoaded;
	public static int energyCapacity, energyMultiplier, rangeWirelessAccessor, itemBoxCapacity, fluidBoxCapacity;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();
		smallFont = config.get(Configuration.CATEGORY_CLIENT, "smallFont", true).getBoolean();
		untouchable = config.get(Configuration.CATEGORY_GENERAL, "invisible=untouchable", true).getBoolean();
		energyNeeded = config.get(Configuration.CATEGORY_GENERAL, "energyNeeded", true).getBoolean();
		energyCapacity = config.get(Configuration.CATEGORY_GENERAL, "energyCapacity", 32000).getInt();
		energyMultiplier = config.get(Configuration.CATEGORY_GENERAL, "energyMultiplier", 15).getInt();
		rangeWirelessAccessor = config.get(Configuration.CATEGORY_GENERAL, "rangeWirelessAccessor", 32).getInt();
		itemBoxCapacity = config.get(Configuration.CATEGORY_GENERAL, "itemBoxCapacity", 200).getInt();
		fluidBoxCapacity = config.get(Configuration.CATEGORY_GENERAL, "fluidBoxCapacity", 64).getInt();
		jeiLoaded = Loader.isModLoaded("JEI");

		if (config.hasChanged()) {
			config.save();
		}
	}

}
