package mrriegel.storagenetwork.tile;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.storagenetwork.ModConfig;
import mrriegel.storagenetwork.item.ItemItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileItemBox extends TileBox<IItemHandler, ItemStack> {

	protected ItemStackHandler handler = new ItemStackHandler(ModConfig.itemboxCapacity) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
			markForSync();
		};
	};

	@Override
	public IItemHandler getStorage() {
		return handler;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		handler.deserializeNBT(NBTHelper.getTag(compound, "handler"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setTag(compound, "handler", handler.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public void writeToStack(ItemStack stack) {
		NBTStackHelper.setTag(stack, "storage", handler.serializeNBT());
	}

	@Override
	public void readFromStack(ItemStack stack) {
		handler.deserializeNBT(NBTStackHelper.getTag(stack, "storage"));
	}

	@Override
	public boolean canTransferItem(ItemStack stack) {
		return ItemItemFilter.canTransferItem(filter, stack);
	}
}
