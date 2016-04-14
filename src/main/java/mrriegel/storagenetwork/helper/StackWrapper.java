package mrriegel.storagenetwork.helper;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackWrapper {
	ItemStack stack;
	int size;

	public StackWrapper(ItemStack stack, int size) {
		super();
		if (stack == null)
			throw new NullPointerException();
		this.stack = stack;
		this.size = size;
	}

	private StackWrapper() {
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound c = compound.getCompoundTag("stack");
		stack = ItemStack.loadItemStackFromNBT(c);
		size = compound.getInteger("size");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag("stack", c);
		compound.setInteger("size", size);
		return compound;
	}

	@Override
	public String toString() {
		return "StackWrapper [stack=" + stack + ", size=" + size + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StackWrapper))
			return false;
		StackWrapper o = (StackWrapper) obj;
		return o.stack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(o.stack, stack);
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		if (stack == null)
			throw new NullPointerException();
		this.stack = stack;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public StackWrapper copy() {
		return new StackWrapper(stack.copy(), size);
	}

	public static StackWrapper loadStackWrapperFromNBT(NBTTagCompound nbt) {
		StackWrapper wrap = new StackWrapper();
		wrap.readFromNBT(nbt);
		return wrap.getStack() != null && wrap.getStack().getItem() != null ? wrap : null;
	}

}
