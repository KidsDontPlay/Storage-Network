package mrriegel.storagenetwork;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.item.ItemItemFilter;
import mrriegel.storagenetwork.item.ItemUpgrade.UpgradeType;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.INetworkStorage;
import mrriegel.storagenetwork.tile.IPriority;
import mrriegel.storagenetwork.tile.IRedstoneActive;
import mrriegel.storagenetwork.tile.TileNetworkCable;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkExporter;
import mrriegel.storagenetwork.tile.TileNetworkImporter;
import mrriegel.storagenetwork.tile.TileNetworkInterface;
import mrriegel.storagenetwork.tile.TileNetworkItemConnection;
import mrriegel.storagenetwork.tile.TileNetworkStock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author canitzp
 */
public class Network {

	public GlobalBlockPos corePosition;
	public Set<INetworkPart> networkParts = Sets.newHashSet();
	public Set<INetworkPart> noCables = Sets.newHashSet();
	public Set<INetworkStorage<?, ?>> storages = Sets.newHashSet();
	public boolean dirtyMap = true, dirtyList = true;

	public Map<FilterItemWrapper, Set<INetworkStorage<IItemHandler, ItemStack>>> itemMap = Maps.newHashMap();

	protected static class FilterItemWrapper {
		public final FilterItem item;

		public FilterItemWrapper(FilterItem item) {
			super();
			this.item = item;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((item.getStack() == null) ? 0 : Item.getIdFromItem(item.getStack().getItem()));
			result = prime * result + ((item.getStack() == null) ? 0 : item.getStack().getItemDamage());
			result = prime * result + ((item.getStack() == null) ? 0 : item.getStack().getTagCompound() != null ? item.getStack().getTagCompound().hashCode() : 0);
			result = prime * result + (item.isMeta() ? 1231 : 1237);
			result = prime * result + (item.isNbt() ? 1231 : 1237);
			result = prime * result + (item.isOre() ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FilterItemWrapper other = (FilterItemWrapper) obj;
			if (item == null) {
				if (other.item != null)
					return false;
			}
			if (ItemHandlerHelper.canItemStacksStack(item.getStack(), other.item.getStack()) && //
					item.isMeta() == other.item.isMeta() && //
					item.isNbt() == other.item.isNbt() && //
					item.isOre() == other.item.isOre())
				return true;
			return false;
		}

	}

	public void addPart(INetworkPart part) {
		networkParts.add(part);
		if (part.getClass() != TileNetworkCable.class)
			noCables.add(part);
		if (part instanceof INetworkStorage<?, ?>)
			storages.add((INetworkStorage<?, ?>) part);
		part.setNetworkCore((TileNetworkCore) corePosition.getTile(null));
	}

	public void removePart(INetworkPart part) {
		networkParts.remove(part);
		if (part.getClass() != TileNetworkCable.class)
			noCables.remove(part);
		if (part instanceof INetworkStorage<?, ?>)
			storages.remove(part);
		part.setNetworkCore(null);
	}

	@Override
	public String toString() {
		return "Network at '" + corePosition.toString() + "'. Data: {" + networkParts.toString() + "}";
	}

	TileNetworkCore getCore() {
		return (TileNetworkCore) corePosition.getTile(null);
	}

	public void onChange() {
		getTileParts(TileNetworkInterface.class).forEach(p -> p.refreshItemhandler());
		dirtyMap = true;
		dirtyList = true;
	}

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

	public <T extends INetworkPart> List<T> getTileParts(Class<T> clazz) {
		return Lists.newArrayList(Iterables.filter(noCables, clazz));
		//		List<T> lis = Lists.newArrayList();
		//		for (INetworkPart part : noCables) {
		//			if (clazz.isInstance(part))
		//				lis.add((T) part);
		//		}
		//		return lis;
	}

	//item start

	public ItemStack requestItem(FilterItem fil, final int size, boolean simulate) {
		if (size == 0 || fil == null)
			return null;
		boolean refresh = itemMap.get(new FilterItemWrapper(fil)) == null || dirtyMap;
		if (refresh)
			dirtyMap = false;
		ItemStack result = null;
		int need = size;
		List<INetworkStorage<?, ?>> networkParts = Lists.newArrayList(refresh ? storages : itemMap.get(new FilterItemWrapper(fil)));
		Collections.sort(networkParts, comparator);
		for (INetworkStorage<?, ?> part : networkParts) {
			if (!(part.getStorage() instanceof IItemHandler))
				continue;
			INetworkStorage<IItemHandler, ItemStack> tile = (INetworkStorage<IItemHandler, ItemStack>) part;
			if (tile instanceof IRedstoneActive && ((IRedstoneActive) tile).isDisabled())
				continue;
			if (tile.canInsert()) {
				IItemHandler inv = tile.getStorage();
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (refresh && itemMap.get(new FilterItemWrapper(fil)) == null)
						itemMap.put(new FilterItemWrapper(fil), Sets.newHashSet());
					if (tile.canTransferItem(stack) && fil.match(stack) && inv.extractItem(i, need, true) != null) {
						if (refresh)
							itemMap.get(new FilterItemWrapper(fil)).add(tile);
						if (result == null) {
							result = inv.extractItem(i, need, simulate);
							need -= result.stackSize;
							if (((INetworkStorage<IItemHandler, ItemStack>) part).getStoragePosition().getTile(null) != null)
								((INetworkStorage<IItemHandler, ItemStack>) part).getStoragePosition().getTile(null).markDirty();
							dirtyList = true;
							dirtyMap = true;
							if (need == 0)
								return result;
						} else {
							if (ItemHandlerHelper.canItemStacksStack(result, stack)) {
								ItemStack ext = inv.extractItem(i, need, simulate);
								int ex = ext.stackSize;
								need -= ex;
								result.stackSize += ex;
								if (((INetworkStorage<IItemHandler, ItemStack>) part).getStoragePosition().getTile(null) != null)
									((INetworkStorage<IItemHandler, ItemStack>) part).getStoragePosition().getTile(null).markDirty();
								dirtyList = true;
								dirtyMap = true;
								if (need == 0)
									return result;
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
		List<INetworkStorage<?, ?>> networkParts = Lists.newArrayList(storages);
		Collections.sort(networkParts, comparator);
		Iterator<INetworkStorage<?, ?>> it = networkParts.iterator();
		while (it.hasNext()) {
			INetworkStorage<?, ?> part = it.next();
			if (!(part.getStorage() instanceof IItemHandler))
				continue;
			INetworkStorage<IItemHandler, ItemStack> tile = (INetworkStorage<IItemHandler, ItemStack>) part;
			if (InvHelper.getAmount(tile.getStorage(), new FilterItem(stack, true, false, true)) <= 0)
				continue;
			it.remove();
			if (tile instanceof IRedstoneActive && ((IRedstoneActive) tile).isDisabled())
				continue;
			if (tile.canExtract() && !tile.getStoragePosition().equals(source)) {
				IItemHandler inv = tile.getStorage();
				if (tile.canTransferItem(stack)) {
					ItemStack restStack = ItemHandlerHelper.insertItemStacked(inv, ItemHandlerHelper.copyStackWithSize(stack, rest), simulate);
					if (tile.getStoragePosition().getTile(null) != null)
						tile.getStoragePosition().getTile(null).markDirty();
					dirtyList = true;
					dirtyMap = true;
					if (restStack == null)
						return null;
					rest = restStack.stackSize;
				}
			}
		}
		for (INetworkStorage<?, ?> part : networkParts) {
			if (!(part.getStorage() instanceof IItemHandler))
				continue;
			INetworkStorage<IItemHandler, ItemStack> tile = (INetworkStorage<IItemHandler, ItemStack>) part;
			if (InvHelper.getAmount(tile.getStorage(), new FilterItem(stack, true, false, true)) > 0)
				continue;
			if (tile instanceof IRedstoneActive && ((IRedstoneActive) tile).isDisabled())
				continue;
			if (tile.canExtract() && !tile.getStoragePosition().equals(source)) {
				IItemHandler inv = tile.getStorage();
				if (tile.canTransferItem(stack)) {
					ItemStack restStack = ItemHandlerHelper.insertItemStacked(inv, ItemHandlerHelper.copyStackWithSize(stack, rest), simulate);
					if (tile.getStoragePosition().getTile(null) != null)
						tile.getStoragePosition().getTile(null).markDirty();
					dirtyList = true;
					dirtyMap = true;
					if (restStack == null)
						return null;
					rest = restStack.stackSize;
				}
			}
		}
		return ItemHandlerHelper.copyStackWithSize(stack, rest);
	}

	public void exportItems() {
		List<INetworkPart> networkParts = Lists.newArrayList(noCables);
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
						if (!getCore().consumeRF(maxInsert * 5 + tile.getUpgradeAmount(UpgradeType.SPEED) * 3, true))
							continue;
						ItemStack req = requestItem(item, maxInsert, false);
						if (req == null)
							continue;
						getCore().consumeRF(req.stackSize * 5 + tile.getUpgradeAmount(UpgradeType.SPEED) * 3, false);
						ItemHandlerHelper.insertItemStacked(inv, req, false);
						if (tile.getTile() != null)
							tile.getTile().markDirty();
						break;
					}
				}
			}
		}
	}

	public void importItems() {
		List<INetworkPart> networkParts = Lists.newArrayList(noCables);
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
						if (!ItemItemFilter.canTransferItem(tile.filter, stack))
							continue;
						ItemStack rest = insertItem(stack, new GlobalBlockPos(tile.getTile().getPos(), tile.getWorld()), true);
						int maxInsert = Math.min(Math.min(stack.stackSize, tile.getTransferAmount(Item.class)), (rest == null ? stack.stackSize : stack.stackSize - rest.stackSize));
						ItemStack ext = inv.extractItem(i, maxInsert, true);
						int ex = ext != null ? ext.stackSize : 0;
						ex = Math.min(ex, maxInsert);
						if (ex == 0 || !getCore().consumeRF(ex * 5 + tile.getUpgradeAmount(UpgradeType.SPEED) * 3, false))
							continue;
						insertItem(inv.extractItem(i, ex, false), new GlobalBlockPos(tile.getTile().getPos(), tile.getWorld()), false);
						if (tile.getTile() != null)
							tile.getTile().markDirty();
						break;
					}
				}
			}
		}
	}

	public void stockItems() {
		List<INetworkPart> networkParts = Lists.newArrayList(noCables);
		Collections.sort(networkParts, comparator);
		for (INetworkPart part : networkParts) {
			if (part instanceof TileNetworkStock) {
				TileNetworkStock tile = (TileNetworkStock) part;
				if (tile.isDisabled() || tile.getWorld().getTotalWorldTime() % (Math.max(1, 30 / tile.getSpeed())) != 0)
					continue;
				if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) != null) {
					IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
					for (int i = 0; i < tile.items.size(); i++) {
						if (tile.items.get(i) == null)
							continue;
						ItemStack s = tile.items.get(i);
						final int stock = tile.numbers.get(i);
						int content = InvHelper.getAmount(inv, new FilterItem(s));
						if (content == stock)
							continue;
						if (content < stock) {
							int maxStacksize = Math.min(s.getMaxStackSize(), tile.getTransferAmount(Item.class));
							maxStacksize = Math.min(maxStacksize, stock - content);
							ItemStack rest = ItemHandlerHelper.insertItemStacked(inv, ItemHandlerHelper.copyStackWithSize(s, maxStacksize), true);
							int maxInsert = maxStacksize - (rest == null ? 0 : rest.stackSize);
							if (!getCore().consumeRF(maxInsert * 5 + tile.getUpgradeAmount(UpgradeType.SPEED) * 3, true))
								continue;
							ItemStack req = requestItem(new FilterItem(s), maxInsert, false);
							if (req == null)
								continue;
							getCore().consumeRF(req.stackSize * 5 + tile.getUpgradeAmount(UpgradeType.SPEED) * 3, false);
							ItemHandlerHelper.insertItemStacked(inv, req, false);
							if (tile.getTile() != null)
								tile.getTile().markDirty();
							break;
						} else {
							ItemStack rest = insertItem(ItemHandlerHelper.copyStackWithSize(s, s.getMaxStackSize()), new GlobalBlockPos(tile.getTile().getPos(), tile.getWorld()), true);
							int maxInsert = Math.min(Math.min(content - stock, tile.getTransferAmount(Item.class)), (rest == null ? s.getMaxStackSize() : s.getMaxStackSize() - rest.stackSize));
							ItemStack ext = InvHelper.extractItem(inv, new FilterItem(s), maxInsert, true);
							int ex = ext != null ? ext.stackSize : 0;
							ex = Math.min(ex, maxInsert);
							if (ex == 0 || !getCore().consumeRF(ex * 5 + tile.getUpgradeAmount(UpgradeType.SPEED) * 3, false))
								continue;
							insertItem(InvHelper.extractItem(inv, new FilterItem(s), maxInsert, false), new GlobalBlockPos(tile.getTile().getPos(), tile.getWorld()), false);
							if (tile.getTile() != null)
								tile.getTile().markDirty();
							break;
						}
					}
				}
			}
		}
	}

	private List<ItemStack> itemStacks;

	public List<ItemStack> getItemstacks() {
		if (itemStacks != null && !dirtyList) {
			return itemStacks;
		}
		List<ItemStack> lis = Lists.newArrayList();
		for (INetworkStorage<?, ?> part : storages) {
			if (!(part.getStorage() instanceof IItemHandler))
				continue;
			INetworkStorage<IItemHandler, ItemStack> tile = (INetworkStorage<IItemHandler, ItemStack>) part;
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
		dirtyList = false;
		return (itemStacks = lis);
	}

	public int getAmountOf(FilterItem filter) {
		int amount = 0;
		for (ItemStack stack : getItemstacks()) {
			if (filter.match(stack))
				amount += stack.stackSize;
		}
		return amount;
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

	@SubscribeEvent
	public static void tick(Close event) {
		Set<IInventory> invs = Sets.newHashSet();
		for (Slot slot : event.getContainer().inventorySlots)
			invs.add(slot.inventory);
		boolean k = false;
		TileNetworkItemConnection cable = null;
		for (IInventory inv : invs)
			if (inv instanceof TileEntity) {
				TileEntity tile = (TileEntity) inv;
				for (EnumFacing f : EnumFacing.VALUES)
					if (tile.getWorld().getTileEntity(tile.getPos().offset(f)) instanceof TileNetworkItemConnection && ((TileNetworkItemConnection) tile.getWorld().getTileEntity(tile.getPos().offset(f))).getNetworkCore() != null) {
						k = true;
						cable = (TileNetworkItemConnection) tile.getWorld().getTileEntity(tile.getPos().offset(f));
						break;
					}
			}
		if (k && cable.getNetworkCore().network != null) {
			cable.getNetworkCore().network.dirtyList = true;
			cable.getNetworkCore().network.dirtyMap = true;
		}
	}

	@SubscribeEvent
	public static void place(NeighborNotifyEvent event) {
		if (event.getWorld().isRemote)
			return;
		if (!event.getWorld().isAirBlock(event.getPos()) && !(event.getWorld().getTileEntity(event.getPos()) instanceof INetworkPart))
			return;
		if (event.getWorld().getTileEntity(event.getPos()) instanceof INetworkPart) {
			boolean invalid = false;
			TileNetworkCore core = null;
			for (EnumFacing face : event.getNotifiedSides()) {
				BlockPos neighbor = event.getPos().offset(face);
				if (event.getWorld().getTileEntity(neighbor) != null) {
					if (event.getWorld().getTileEntity(neighbor) instanceof INetworkPart) {
						INetworkPart part = (INetworkPart) event.getWorld().getTileEntity(neighbor);
						if (part.getNeighborFaces().contains(face.getOpposite()) && ((INetworkPart) event.getWorld().getTileEntity(event.getPos())).getNeighborFaces().contains(face))
							if (part.getNetworkCore() != null) {
								if (core == null) {
									core = part.getNetworkCore();
								} else {
									if (!core.getPos().equals(part.getNetworkCore().getPos()))
										invalid = true;
								}
							} else
								invalid = true;
					} else
						invalid = true;
				}
			}
			if (!invalid && core != null && core.network != null) {
				core.network.addPart((INetworkPart) event.getWorld().getTileEntity(event.getPos()));
				core.network.onChange();
				return;
			}
		}

		for (EnumFacing face : event.getNotifiedSides()) {
			BlockPos neighbor = event.getPos().offset(face);
			TileEntity tile = event.getWorld().getTileEntity(neighbor);
			if (!(event.getWorld().getTileEntity(event.getPos()) instanceof INetworkPart) && !event.getWorld().isAirBlock(event.getPos()) && tile != null && tile.getClass() == TileNetworkCable.class)
				continue;
			if (tile instanceof INetworkPart && ((INetworkPart) tile).getNetworkCore() != null && ((INetworkPart) tile).getNeighborFaces().contains(face.getOpposite())) {
				((INetworkPart) tile).getNetworkCore().markForNetworkInit();
				BlockNetworkCable.releaseNetworkParts(event.getWorld(), tile.getPos(), ((INetworkPart) tile).getNetworkCore().getPos());
			} else if (tile instanceof TileNetworkCore) {
				((TileNetworkCore) tile).markForNetworkInit();
				BlockNetworkCable.releaseNetworkParts(event.getWorld(), tile.getPos(), tile.getPos());
			}
		}
	}

}
