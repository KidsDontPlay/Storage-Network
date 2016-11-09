package mrriegel.storagenetwork;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {

	public static Configuration config;

	public static boolean needsEnergy;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		needsEnergy = config.getBoolean("needsEnergy", Configuration.CATEGORY_GENERAL, true, "If enabled the network core needs energy to operate.");

		if (config.hasChanged()) {
			config.save();
		}
	}
}
