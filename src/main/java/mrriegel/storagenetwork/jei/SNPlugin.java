package mrriegel.storagenetwork.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class SNPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RequestRecipeTransferHandler());
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(new TemplateRecipeTransferHandler());
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.request), VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		// TODO Auto-generated method stub

	}
}
