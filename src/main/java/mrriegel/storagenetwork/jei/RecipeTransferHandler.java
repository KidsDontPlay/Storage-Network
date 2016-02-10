package mrriegel.storagenetwork.jei;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.ingredients.GuiIngredient;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.network.ClearMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.RecipeMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class RecipeTransferHandler implements IRecipeTransferHandler {
	@Override
	public Class<? extends Container> getContainerClass() {
		return ContainerRequest.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if (doTransfer) {
			PacketHandler.INSTANCE.sendToServer(new ClearMessage());
			Map inputs = recipeLayout.getItemStacks().getGuiIngredients();
			Map<Integer, List<ItemStack>> map = new HashMap<Integer, List<ItemStack>>();
			for (int j = 0; j < container.inventorySlots.size(); j++) {
				Slot slot = container.inventorySlots.get(j);
				if ((slot.inventory instanceof InventoryCrafting)) {
					GuiIngredient ingredient = (GuiIngredient) inputs.get(slot.getSlotIndex() + 1);
					if (ingredient != null) {
						map.put(j, ingredient.getAllIngredients());

					}
				}
			}
			NBTTagCompound nbt = new NBTTagCompound();
			for (int j = 1; j < 10; j++) {
				NBTTagList invList = new NBTTagList();
				if (map.get(j) != null)
					for (int i = 0; i < map.get(j).size(); i++) {
						if (map.get(j).get(i) != null) {
							NBTTagCompound stackTag = new NBTTagCompound();
							map.get(j).get(i).writeToNBT(stackTag);
							invList.appendTag(stackTag);
						}
					}
				nbt.setTag("s" + j, invList);
			}
			PacketHandler.INSTANCE.sendToServer(new RecipeMessage(nbt));
		}
		return null;
	}

}
