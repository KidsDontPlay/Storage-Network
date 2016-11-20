package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.FilterItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class TileItemOverflowDevice extends TileNetworkPart {

	public List<ItemStack> items = Lists.newArrayList(null, null, null, null, null, null, null, null);
	public List<Integer> numbers = Lists.newArrayList(0, 0, 0, 0, 0, 0, 0, 0);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		items = NBTHelper.getItemStackList(compound, "items");
		numbers = NBTHelper.getIntList(compound, "numbers");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setItemStackList(compound, "items", items);
		NBTHelper.setIntList(compound, "numbers", numbers);
		return super.writeToNBT(compound);
	}

	public boolean containsItem(ItemStack stack) {
		for (ItemStack item : items)
			if (new FilterItem(stack).match(item))
				return true;
		return false;
	}

	public int getMax(ItemStack stack) {
		int max = 0;
		for (int i = 0; i < items.size(); i++) {
			ItemStack item = items.get(i);
			if (new FilterItem(stack).match(item))
				max = Math.max(max, numbers.get(i));
		}
		return max;
	}

}
