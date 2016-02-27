package mrriegel.storagenetwork.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FilterItem {
	ItemStack stack;
	boolean meta, ore;

	public FilterItem(ItemStack stack, boolean meta, boolean ore) {
		this.stack = stack;
		this.meta = meta;
		this.ore = ore;
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound c = compound.getCompoundTag("stack");
		stack = ItemStack.loadItemStackFromNBT(c);
		meta = compound.getBoolean("meta");
		ore = compound.getBoolean("ore");
	}

	public void writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag("stack", c);
		compound.setBoolean("meta", meta);
		compound.setBoolean("ore", ore);
	}

	@Override
	public String toString() {
		return "FilterItem [stack=" + stack + ", meta=" + meta + ", ore=" + ore + "]";
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public boolean isMeta() {
		return meta;
	}

	public void setMeta(boolean meta) {
		this.meta = meta;
	}

	public boolean isOre() {
		return ore;
	}

	public void setOre(boolean ore) {
		this.ore = ore;
	}

	public static FilterItem loadFilterItemFromNBT(NBTTagCompound nbt) {
		FilterItem fil = new FilterItem(null, false, false);
		fil.readFromNBT(nbt);
		return fil.getStack() != null && fil.getStack().getItem() != null ? fil : null;
	}
}
