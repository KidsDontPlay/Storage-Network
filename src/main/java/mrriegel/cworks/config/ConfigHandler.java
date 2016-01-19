package mrriegel.cworks.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static double jetpackMaxVerticalSpeed, jetpackAcceleration,
			jetpackMaxHorizontalSpeed;
	public static int jetpackMaxFuel, fuelValueLava;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();
		jetpackMaxVerticalSpeed = config.get(Configuration.CATEGORY_GENERAL,
				"jetpackMaxVerticalSpeed", 0.5).getDouble();
		jetpackAcceleration = config.get(Configuration.CATEGORY_GENERAL,
				"jetpackAcceleration", 0.15).getDouble();
		jetpackMaxHorizontalSpeed = config.get(Configuration.CATEGORY_GENERAL,
				"jetpackMaxHorizontalSpeed", 0.25).getDouble();
		jetpackMaxFuel = config.get(Configuration.CATEGORY_GENERAL,
				"jetpackMaxFuel", 10000).getInt();
		fuelValueLava = config.get(Configuration.CATEGORY_GENERAL,
				"fuelValueLava", 2500).getInt();
		if (config.hasChanged()) {
			config.save();
		}
	}

}
