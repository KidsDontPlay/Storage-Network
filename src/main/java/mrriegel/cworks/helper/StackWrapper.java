package mrriegel.cworks.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackWrapper {
	ItemStack stack;
	int size;

	public StackWrapper(ItemStack stack, int size) {
		super();
		this.stack = stack;
		this.size = size;
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound c = compound.getCompoundTag("stack");
		stack = ItemStack.loadItemStackFromNBT(c);
		size = compound.getInteger("size");
	}

	public void writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag("stack", c);
		compound.setInteger("size", size);
	}

	@Override
	public String toString() {
		return "StackWrapper [stack=" + stack + ", size=" + size + "]";
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
