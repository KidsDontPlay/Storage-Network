package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public interface INetworkStorage<T, S> {

	T getStorage();

	GlobalBlockPos getStoragePosition();

	boolean canInsert();

	boolean canExtract();

	boolean canTransferItem(S stack);

	public static class ItemHandlerWrapper implements IItemHandler, IFluidHandler {

		List<INetworkStorage<IItemHandler, ItemStack>> storages;
		TileNetworkCore core;
		protected final int[] baseIndex;
		protected final int slotCount;

		public ItemHandlerWrapper(TileNetworkCore core) {
			List<INetworkStorage<IItemHandler, ItemStack>> lis = Lists.newArrayList();
			for (INetworkPart part : core.network.noCables) {
				if (part instanceof INetworkStorage && ((INetworkStorage<?, ?>) part).getStorage() instanceof IItemHandler) {
					lis.add((INetworkStorage<IItemHandler, ItemStack>) part);
				}
			}
			this.core = core;
			this.storages = lis;
			this.storages = Lists.newArrayList(Iterables.filter(this.storages, n -> n != null && n.getStorage() != null));
			this.baseIndex = new int[this.storages.size()];
			int index = 0;
			for (int i = 0; i < this.storages.size(); i++) {
				index += this.storages.get(i).getStorage().getSlots();
				baseIndex[i] = index;
			}
			this.slotCount = index;
		}

		protected int getIndexForSlot(int slot) {
			if (slot < 0)
				return -1;
			for (int i = 0; i < baseIndex.length; i++) {
				if (slot - baseIndex[i] < 0) {
					return i;
				}
			}
			return -1;
		}

		protected INetworkStorage<IItemHandler, ItemStack> getStorageFromIndex(int index) {
			if (index < 0 || index >= storages.size()) {
				return null;
			}
			return storages.get(index);
		}

		protected int getSlotFromIndex(int slot, int index) {
			if (index <= 0 || index >= baseIndex.length) {
				return slot;
			}
			return slot - baseIndex[index - 1];
		}

		@Override
		public int getSlots() {
			//			return slotCount;
			if (core == null || core.network == null)
				return 0;
			return core.network.getItemstacks().size() + 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			//			int index = getIndexForSlot(slot);
			//			INetworkStorage<IItemHandler, ItemStack> storage = getStorageFromIndex(index);
			//			if (storage == null)
			//				return null;
			//			if (storage instanceof IRedstoneActive && ((IRedstoneActive) storage).isDisabled())
			//				return null;
			//			slot = getSlotFromIndex(slot, index);
			//			return storage.getStorage().getStackInSlot(slot);
			if (core == null || core.network == null)
				return null;
			List<ItemStack> lis = core.network.getItemstacks();
			if (lis.isEmpty() || slot == lis.size() - 1)
				return null;
			return lis.get(Math.min(slot, lis.size() - 1));
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			//			int index = getIndexForSlot(slot);
			//			INetworkStorage<IItemHandler, ItemStack> storage = getStorageFromIndex(index);
			//			if (storage == null || !storage.canExtract() || !storage.canTransferItem(stack))
			//				return stack;
			//			if (storage instanceof IRedstoneActive && ((IRedstoneActive) storage).isDisabled())
			//				return stack;
			//			slot = getSlotFromIndex(slot, index);
			//			return storage.getStorage().insertItem(slot, stack, simulate);
			if (core == null || core.network == null)
				return stack;
			return core.network.insertItem(stack, null, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			//			int index = getIndexForSlot(slot);
			//			INetworkStorage<IItemHandler, ItemStack> storage = getStorageFromIndex(index);
			//			if (storage == null || !storage.canInsert() || !storage.canTransferItem(getStackInSlot(slot)))
			//				return null;
			//			if (storage instanceof IRedstoneActive && ((IRedstoneActive) storage).isDisabled())
			//				return null;
			//			slot = getSlotFromIndex(slot, index);
			//			return storage.getStorage().extractItem(slot, amount, simulate);
			if (core == null || core.network == null)
				return null;
			ItemStack inSlot = getStackInSlot(slot);
			if (inSlot != null)
				return core.network.requestItem(new FilterItem(inSlot, true, false, true), amount, simulate);
			else
				return null;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			if (core == null || core.network == null)
				return new IFluidTankProperties[0];
			List<FluidStack> lis = core.network.getFluidstacks();
			List<FluidTankProperties> props = Lists.newArrayList();
			for (FluidStack f : lis)
				props.add(new FluidTankProperties(f, f.amount, false, false));
			return Iterables.toArray(props, FluidTankProperties.class);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return null;
		}

	}
}
