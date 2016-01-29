package mrriegel.storagenetwork.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static boolean smallFont, lavaNeeded;
	public static int lavaCapacity, energyMultilplier;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		smallFont = config
				.get(Configuration.CATEGORY_CLIENT, "smallFont", true)
				.getBoolean();
		lavaNeeded = config.get(Configuration.CATEGORY_GENERAL, "lavaNeeded",
				true).getBoolean();
		lavaCapacity = config.get(Configuration.CATEGORY_GENERAL,
				"lavaCapacity", 8).getInt();
		energyMultilplier = config.get(Configuration.CATEGORY_GENERAL,
				"energyMultilplier", 3).getInt();

		if (config.hasChanged()) {
			config.save();
		}
	}

}
