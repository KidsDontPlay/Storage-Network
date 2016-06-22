package mrriegel.storagenetwork.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class FilterItem {
	ItemStack stack;
	boolean meta, ore, nbt;

	public FilterItem(ItemStack stack) {
		this(stack, stack != null ? stack.getItemDamage() != OreDictionary.WILDCARD_VALUE : true, false, false);
	}

	public FilterItem(ItemStack stack, boolean meta, boolean ore, boolean nbt) {
		if (stack == null)
			throw new NullPointerException();
		this.stack = stack;
		this.meta = meta;
		this.ore = ore;
		this.nbt = nbt;
	}

	private FilterItem() {
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound c = compound.getCompoundTag("stack");
		stack = ItemStack.loadItemStackFromNBT(c);
		meta = compound.getBoolean("meta");
		ore = compound.getBoolean("ore");
		nbt = compound.getBoolean("nbt");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag("stack", c);
		compound.setBoolean("meta", meta);
		compound.setBoolean("ore", ore);
		compound.setBoolean("nbt", nbt);
		return c;
	}

	@Override
	public String toString() {
		return "FilterItem [stack=" + stack + ", meta=" + meta + ", ore=" + ore + ", nbt=" + nbt + "]";
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		if (stack == null)
			throw new NullPointerException();
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

	public boolean isNbt() {
		return nbt;
	}

	public void setNbt(boolean nbt) {
		this.nbt = nbt;
	}

	public static FilterItem loadFilterItemFromNBT(NBTTagCompound nbt) {
		FilterItem fil = new FilterItem();
		fil.readFromNBT(nbt);
		return fil.getStack() != null && fil.getStack().getItem() != null ? fil : null;
	}

	public boolean match(ItemStack s) {
		if (s == null)
			return false;
		if (ore && Util.equalOreDict(s, stack))
			return true;
		if (nbt && !ItemStack.areItemStackTagsEqual(stack, s))
			return false;
		if (meta && s.getItemDamage() != stack.getItemDamage())
			return false;
		return s.getItem() == stack.getItem();
	}
}