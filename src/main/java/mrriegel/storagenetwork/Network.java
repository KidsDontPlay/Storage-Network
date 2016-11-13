package mrriegel.storagenetwork;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.item.ItemItemFilter;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.INetworkStorage;
import mrriegel.storagenetwork.tile.IPriority;
import mrriegel.storagenetwork.tile.IRedstoneActive;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkExporter;
import mrriegel.storagenetwork.tile.TileNetworkImporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author canitzp
 */
public class Network {

	public GlobalBlockPos corePosition;
	public Set<INetworkPart> networkParts = Sets.newHashSet();

	public void addPart(INetworkPart part) {
		networkParts.add(part);
		part.setNetworkCore((TileNetworkCore) corePosition.getTile(null));
	}

	public void removePart(INetworkPart part) {
		networkParts.remove(part);
		part.setNetworkCore(null);
	}

	@Override
	public String toString() {
		return "Network at '" + corePosition.toString() + "'. Data: {" + networkParts.toString() + "}";
	}

	//item start

	Comparator<INetworkPart> comparator = new Comparator<INetworkPart>() {
		@Override
		public int compare(INetworkPart o1, INetworkPart o2) {
			if (o1 instanceof IPriority && o2 instanceof IPriority)
				return Integer.compare(((IPriority) o2).getPriority(), ((IPriority) o1).getPriority());
			else if (o1 instanceof IPriority)
				return -1;
			else if (o2 instanceof IPriority)
				return 1;
			else
				return 0;
		}
	};

	public ItemStack requestItem(FilterItem fil, final int size, boolean simulate) {
		if (size == 0 || fil == null)
			return null;
		ItemStack result = null;
		int need = size;
		List<INetworkPart> networkParts = Lists.newArrayList(this.networkParts);
		Collections.sort(networkParts, comparator);
		for (INetworkPart part : networkParts) {
			if (part instanceof INetworkStorage) {
				if (!(((INetworkStorage<?>) part).getStorage() instanceof IItemHandler))
					continue;
				INetworkStorage<IItemHandler> tile = (INetworkStorage<IItemHandler>) part;
				if (tile instanceof IRedstoneActive && ((IRedstoneActive) tile).isDisabled())
					continue;
				if (tile.canInsert()) {
					IItemHandler inv = tile.getStorage();
					for (int i = 0; i < inv.getSlots(); i++) {
						ItemStack stack = inv.getStackInSlot(i);
						if (tile.canTransferItem(stack) && fil.match(stack)) {
							if (result == null) {
								result = inv.extractItem(i, need, simulate);
								need -= result.stackSize;
								if (need == 0)
									return result;
							} else {
								if (ItemHandlerHelper.canItemStacksStack(result, stack)) {
									ItemStack ext = inv.extractItem(i, need, simulate);
									int ex = ext.stackSize;
									need -= ex;
									result.stackSize += ex;
									if (need == 0)
										return result;
								}

							}
						}
					}
				}
			}
		}
		return result;
	}

	public ItemStack insertItem(ItemStack stack, GlobalBlockPos source, boolean simulate) {
		if (stack == null)
			return null;
		int rest = stack.stackSize;
		List<INetworkPart> networkParts = Lists.newArrayList(this.networkParts);
		Collections.sort(networkParts, comparator);
		for (INetworkPart part : networkParts) {
			if (part instanceof INetworkStorage) {
				if (!(((INetworkStorage<?>) part).getStorage() instanceof IItemHandler))
					continue;
				INetworkStorage<IItemHandler> tile = (INetworkStorage<IItemHandler>) part;
				if (tile instanceof IRedstoneActive && ((IRedstoneActive) tile).isDisabled())
					continue;
				if (tile.canExtract() && !tile.getStoragePosition().equals(source)) {
					IItemHandler inv = tile.getStorage();
					if (tile.canTransferItem(stack)) {
						ItemStack restStack = ItemHandlerHelper.insertItemStacked(inv, ItemHandlerHelper.copyStackWithSize(stack, rest), simulate);
						if (restStack == null)
							return null;
						rest = restStack.stackSize;
					}
				}
			}
		}
		return ItemHandlerHelper.copyStackWithSize(stack, rest);
	}

	public void exportItems() {
		List<INetworkPart> networkParts = Lists.newArrayList(this.networkParts);
		Collections.sort(networkParts, comparator);
		for (INetworkPart part : networkParts) {
			if (part instanceof TileNetworkExporter) {
				TileNetworkExporter tile = (TileNetworkExporter) part;
				if (tile.filter == null || tile.isDisabled() || (tile.getWorld().getTotalWorldTime() + 15) % (Math.max(1, 30 / tile.getSpeed())) != 0)
					continue;
				if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) != null) {
					IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
					for (FilterItem item : ItemItemFilter.getFilterItems(tile.filter)) {
						int maxStacksize = Math.min(item.getStack().getMaxStackSize(), tile.getTransferAmount(Item.class));
						ItemStack rest = ItemHandlerHelper.insertItemStacked(inv, ItemHandlerHelper.copyStackWithSize(item.getStack(), maxStacksize), true);
						int maxInsert = maxStacksize - (rest == null ? 0 : rest.stackSize);
						ItemStack req = requestItem(item, maxInsert, false);
						if (req == null)
							continue;
						ItemHandlerHelper.insertItemStacked(inv, req, false);
						break;

					}
				}

			}
		}
	}

	public void importItems() {
		List<INetworkPart> networkParts = Lists.newArrayList(this.networkParts);
		Collections.sort(networkParts, comparator);
		for (INetworkPart part : networkParts) {
			if (part instanceof TileNetworkImporter) {
				TileNetworkImporter tile = (TileNetworkImporter) part;
				if (tile.isDisabled() || tile.getWorld().getTotalWorldTime() % (Math.max(1, 30 / tile.getSpeed())) != 0)
					continue;
				if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) != null) {
					IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
					for (int i = 0; i < inv.getSlots(); i++) {
						ItemStack stack = inv.getStackInSlot(i);
						if (stack == null)
							continue;
						if (!ItemItemFilter.canTransferItem(tile.filter, stack))
							continue;
						ItemStack ins = insertItem(stack, new GlobalBlockPos(tile.getTile().getPos(), tile.getWorld()), true);
						int maxInsert = Math.min(stack.stackSize, tile.getTransferAmount(Item.class)) - (ins == null ? 0 : ins.stackSize);
						ItemStack ext = inv.extractItem(i, maxInsert, true);
						int ex = ext != null ? ext.stackSize : 0;
						ex = Math.min(ex, maxInsert);
						if (ex == 0)
							continue;
						insertItem(inv.extractItem(i, ex, false), new GlobalBlockPos(tile.getTile().getPos(), tile.getWorld()), false);
						break;

					}
				}
			}
		}
	}

	public List<ItemStack> getItemstacks() {
		List<ItemStack> lis = Lists.newArrayList();
		for (INetworkPart part : networkParts) {
			if (part instanceof INetworkStorage) {
				if (!(((INetworkStorage<?>) part).getStorage() instanceof IItemHandler))
					continue;
				INetworkStorage<IItemHandler> tile = (INetworkStorage<IItemHandler>) part;
				if (tile instanceof IRedstoneActive && ((IRedstoneActive) tile).isDisabled())
					continue;
				if (tile.canInsert()) {
					IItemHandler inv = tile.getStorage();
					for (int i = 0; i < inv.getSlots(); i++) {
						ItemStack stack = inv.getStackInSlot(i);
						if (tile.canTransferItem(stack))
							lis.add(stack);
					}
				}
			}
		}
		return lis;
	}

	//item end

	//fluid start

	public ItemStack requestFluid(FluidStack fluid, final int size, boolean simulate) {
		if (size == 0 || fluid == null)
			return null;
		return null;
	}

	public int insertFluid(FluidStack fluid, BlockPos source, boolean simulate) {
		return 0;
	}

	public void exportFluids() {

	}

	public void importFluids() {

	}

	public List<FluidStack> getFluidstacks() {
		List<FluidStack> lis = Lists.newArrayList();

		return lis;
	}

	//fluid end

}
