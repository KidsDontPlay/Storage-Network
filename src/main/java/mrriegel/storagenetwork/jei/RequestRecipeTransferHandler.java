package mrriegel.storagenetwork.jei;

import java.util.Map;

import mezz.jei.Internal;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.storagenetwork.container.ContainerAbstractRequest;
import mrriegel.storagenetwork.message.MessageRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RequestRecipeTransferHandler implements IRecipeTransferHandler<ContainerAbstractRequest> {

	@Override
	public Class<ContainerAbstractRequest> getContainerClass() {
		return ContainerAbstractRequest.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeTransferError transferRecipe(ContainerAbstractRequest container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if (doTransfer) {
			Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs = recipeLayout.getItemStacks().getGuiIngredients();
			NBTTagCompound nbt = new NBTTagCompound();
			for (int i = 1; i < 10; i++) {
				if (Internal.getStackHelper().getOreDictEquivalent(inputs.get(i).getAllIngredients()) != null)
					NBTHelper.setString(nbt, i - 1 + "s", Internal.getStackHelper().getOreDictEquivalent(inputs.get(i).getAllIngredients()));
				else
					NBTHelper.setItemStackList(nbt, i - 1 + "l", inputs.get(i).getAllIngredients());
			}
			nbt.setInteger("button", 2000);
			PacketHandler.sendToServer(new MessageRequest(nbt));
		}
		return null;
	}

}
