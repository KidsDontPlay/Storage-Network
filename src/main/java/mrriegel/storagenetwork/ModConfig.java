package mrriegel.storagenetwork;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {

	public static Configuration config;

	public static boolean needsEnergy, STOPTICK, teleportItems;
	public static int rangeWirelessAccessor, itemboxCapacity, energycellCapacity, energyinterfaceTransferRate;
	public static float energyMultiplier;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		needsEnergy = config.getBoolean("needsEnergy", Configuration.CATEGORY_GENERAL, true, "If enabled the network core needs energy to operate.");
		teleportItems = config.getBoolean("teleportItems", Configuration.CATEGORY_GENERAL, false, "If enabled items will be teleported to the item attractor.");
		STOPTICK = config.getBoolean("STOPTICK", "DEBUG", false, "If enabled the network won't work. For the case that you cannot enter your world.");
		rangeWirelessAccessor = config.getInt("rangeWirelessAccessor", Configuration.CATEGORY_GENERAL, 64, 8, 256, "The range the wireless accessor works within.");
		itemboxCapacity = config.getInt("itemboxCapacity", Configuration.CATEGORY_GENERAL, 100, 25, 99999, "Capacity of item box.");
		energycellCapacity = config.getInt("energycellCapacity", Configuration.CATEGORY_GENERAL, 500000, 10000, 8000000, "Capacity of item box.");
		energyinterfaceTransferRate = config.getInt("energyinterfaceTransferRate", Configuration.CATEGORY_GENERAL, 800, 80, 80000, "Transfer rate per energy cell.");
		energyMultiplier = config.getFloat("energyMultiplier", Configuration.CATEGORY_GENERAL, 1.0F, 0.1F, 100F, "How much energy the network drains.");

		if (config.hasChanged()) {
			config.save();
		}
	}
}
