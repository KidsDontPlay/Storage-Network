package mrriegel.storagenetwork.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static int maxCable;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();
		maxCable = config.get(Configuration.CATEGORY_GENERAL, "maxCable", 1000)
				.getInt();
		if (config.hasChanged()) {
			config.save();
		}
	}

}
