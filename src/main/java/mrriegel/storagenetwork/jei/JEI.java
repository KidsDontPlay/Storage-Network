package mrriegel.storagenetwork.jei;

import mezz.jei.Internal;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.gui.Focus;
import mrriegel.storagenetwork.container.ContainerRequestItem;
import mrriegel.storagenetwork.container.ContainerRequestTable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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

	public static boolean hasKeyboardFocus() {
		if (Internal.getRuntime().getItemListOverlay() != null && Internal.getRuntime().getItemListOverlay().getInternal() != null)
			return Internal.getRuntime().getItemListOverlay().getInternal().hasKeyboardFocus();
		return false;
	}

	public static void setFilterText(String s) {
		if (Internal.getRuntime().getItemListOverlay() != null)
			Internal.getRuntime().getItemListOverlay().setFilterText(s);
	}

	public static void showRecipes(ItemStack stack) {
		Internal.getRuntime().getRecipesGui().show(new Focus<ItemStack>(IFocus.Mode.OUTPUT, stack));
	}

	public static void showUsage(ItemStack stack) {
		Internal.getRuntime().getRecipesGui().show(new Focus<ItemStack>(IFocus.Mode.INPUT, stack));
	}

	public static void showRecipes(FluidStack stack) {
		Internal.getRuntime().getRecipesGui().show(new Focus<FluidStack>(IFocus.Mode.OUTPUT, stack));
	}

	public static void showUsage(FluidStack stack) {
		Internal.getRuntime().getRecipesGui().show(new Focus<FluidStack>(IFocus.Mode.INPUT, stack));
	}

}
