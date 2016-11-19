package mrriegel.storagenetwork.tile;

import java.util.List;

import com.google.common.collect.Lists;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

}
