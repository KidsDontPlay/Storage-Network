package mrriegel.storagenetwork.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static boolean smallFont;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		smallFont = config
				.get(Configuration.CATEGORY_CLIENT, "smallFont", true)
				.getBoolean();

		if (config.hasChanged()) {
			config.save();
		}
	}

}
