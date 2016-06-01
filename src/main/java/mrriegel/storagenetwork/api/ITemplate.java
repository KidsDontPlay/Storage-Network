package mrriegel.storagenetwork.api;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface ITemplate {
	public Map<Integer, ItemStack> getStacks(ItemStack stack);
	public Map<Integer, Boolean> getMetas(ItemStack stack);
	public Map<Integer, Boolean> getOres(ItemStack stack);
}
