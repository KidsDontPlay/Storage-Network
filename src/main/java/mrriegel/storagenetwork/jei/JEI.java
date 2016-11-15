package mrriegel.storagenetwork.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mrriegel.storagenetwork.container.ContainerRequestItem;
import mrriegel.storagenetwork.container.ContainerRequestTable;

@JEIPlugin
public class JEI implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {

	}

	@Override
	public void register(IModRegistry registry) {
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RequestRecipeTransferHandler<ContainerRequestItem>(ContainerRequestItem.class), VanillaRecipeCategoryUid.CRAFTING);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RequestRecipeTransferHandler<ContainerRequestTable>(ContainerRequestTable.class), VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

	}

}
