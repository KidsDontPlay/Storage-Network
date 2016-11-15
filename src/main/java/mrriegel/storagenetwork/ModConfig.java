package mrriegel.storagenetwork;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {

	public static Configuration config;

	public static boolean needsEnergy;
	public static int rangeWirelessAccessor;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		needsEnergy = config.getBoolean("needsEnergy", Configuration.CATEGORY_GENERAL, true, "If enabled the network core needs energy to operate.");
		rangeWirelessAccessor = config.getInt("rangeWirelessAccessor", Configuration.CATEGORY_GENERAL, 32, 8, 128, "The range the wireless accessor works within.");

		if (config.hasChanged()) {
			config.save();
		}
	}
}
