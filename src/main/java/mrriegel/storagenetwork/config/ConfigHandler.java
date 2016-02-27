package mrriegel.storagenetwork.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static boolean smallFont, energyNeeded, untouchable;
	public static int energyCapacity, energyMultiplier, rangeWirelessAccessor;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		smallFont = config.get(Configuration.CATEGORY_CLIENT, "smallFont", true).getBoolean();
		untouchable = config.get(Configuration.CATEGORY_GENERAL, "invisible=untouchable", true).getBoolean();
		energyNeeded = config.get(Configuration.CATEGORY_GENERAL, "energyNeeded", true).getBoolean();
		energyCapacity = config.get(Configuration.CATEGORY_GENERAL, "energyCapacity", 32000).getInt();
		energyMultiplier = config.get(Configuration.CATEGORY_GENERAL, "energyMultiplier", 20).getInt();
		rangeWirelessAccessor = config.get(Configuration.CATEGORY_GENERAL, "rangeWirelessAccessor", 32).getInt();

		if (config.hasChanged()) {
			config.save();
		}
	}

}
