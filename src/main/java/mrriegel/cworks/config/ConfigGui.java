package mrriegel.cworks.config;

import mrriegel.cworks.CableWorks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGui extends GuiConfig {
	public ConfigGui(GuiScreen parent) {
		super(parent, new ConfigElement(
				ConfigHandler.config
						.getCategory(Configuration.CATEGORY_GENERAL))
				.getChildElements(), CableWorks.MODID, false, false,
				CableWorks.MODNAME + " Configs");
	}

}
