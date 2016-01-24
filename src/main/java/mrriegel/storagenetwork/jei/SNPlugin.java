package mrriegel.storagenetwork.jei;

import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class SNPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		RecipeTransferHandler handler = new RecipeTransferHandler();
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(handler);
	}

	@Override
	public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
	}

	@Override
	public void onItemRegistryAvailable(IItemRegistry itemRegistry) {
	}

	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {
	}
}
