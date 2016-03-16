package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.helper.CraftingTask;
import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

public class TileMaster extends TileEntity implements ITickable, IEnergyReceiver {
	public List<BlockPos> connectables, storageInventorys, imInventorys, exInventorys, fstorageInventorys, fimInventorys, fexInventorys;
	public EnergyStorage en = new EnergyStorage(ConfigHandler.energyCapacity, 400, 0);
	public List<CraftingTask> tasks = new ArrayList<CraftingTask>();

	public List<FluidStack> getFluids() {
		List<FluidStack> stacks = new ArrayList<FluidStack>();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.fstorageKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IFluidHandler) {
				invs.add(tile);
			}
		}
		for (TileKabel t : invs) {
			IFluidHandler inv = (IFluidHandler) worldObj.getTileEntity(t.getConnectedInventory());
			if (inv == null)
				continue;
			if (inv.getTankInfo(t.getInventoryFace().getOpposite()) == null)
				continue;
			for (FluidTankInfo i : inv.getTankInfo(t.getInventoryFace().getOpposite())) {
				if (i != null && i.fluid != null && t.canTransfer(i.fluid.getFluid()))
					addToList(stacks, i.fluid.getFluid(), i.fluid.amount);
			}

		}
		return stacks;
	}

	public List<StackWrapper> getStacks() {
		List<StackWrapper> stacks = new ArrayList<StackWrapper>();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.storageKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IInventory) {
				invs.add(tile);
			}
		}
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if (inv == null)
				continue;
			else if (inv instanceof IDrawerGroup) {
				IDrawerGroup group = (IDrawerGroup) inv;
				for (int i = 0; i < group.getDrawerCount(); i++) {
					if (!group.isDrawerEnabled(i))
						continue;
					IDrawer drawer = group.getDrawer(i);
					ItemStack stack = drawer.getStoredItemPrototype();
					if (stack != null && stack.getItem() != null && t.canTransfer(stack)) {
						addToList(stacks, stack.copy(), drawer.getStoredItemCount());
					}
				}
			} else if (inv instanceof ISidedInventory) {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t.getInventoryFace().getOpposite())) {
					if (inv.getStackInSlot(i) != null && t.canTransfer(inv.getStackInSlot(i)) && ((ISidedInventory) inv).canExtractItem(i, inv.getStackInSlot(i), t.getInventoryFace().getOpposite())) {
						addToList(stacks, inv.getStackInSlot(i).copy(), inv.getStackInSlot(i).stackSize);
					}
				}
			} else {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					if (inv.getStackInSlot(i) != null && t.canTransfer(inv.getStackInSlot(i)))
						addToList(stacks, inv.getStackInSlot(i).copy(), inv.getStackInSlot(i).stackSize);
				}
			}

		}
		return stacks;
	}

	public List<StackWrapper> getCraftableStacks() {
		List<StackWrapper> craftableStacks = new ArrayList<StackWrapper>();
		List<TileContainer> invs = new ArrayList<TileContainer>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileContainer))
				continue;
			TileContainer tile = (TileContainer) worldObj.getTileEntity(p);
			invs.add(tile);
		}
		List<StackWrapper> stacks = getStacks();
		for (TileContainer t : invs) {
			for (int i = 0; i < t.getSizeInventory(); i++) {
				if (t.getStackInSlot(i) != null) {
					NBTTagCompound res = (NBTTagCompound) t.getStackInSlot(i).getTagCompound().getTag("res");
					if (!Util.contains(stacks, new StackWrapper(ItemStack.loadItemStackFromNBT(res), 0), new Comparator<StackWrapper>() {
						@Override
						public int compare(StackWrapper o1, StackWrapper o2) {
							if (o1.getStack().isItemEqual(o2.getStack()) && ItemStack.areItemStackTagsEqual(o2.getStack(), o1.getStack())) {
								return 0;
							}
							return 1;
						}
					}))
						addToList(craftableStacks, ItemStack.loadItemStackFromNBT(res), 0);
				}
			}
		}
		return craftableStacks;
	}

	private void addToList(List<StackWrapper> lis, ItemStack s, int num) {
		boolean added = false;
		for (int i = 0; i < lis.size(); i++) {
			ItemStack stack = lis.get(i).getStack();
			if (s.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, s)) {
				lis.get(i).setSize(lis.get(i).getSize() + num);
				added = true;
			}
		}
		if (!added)
			lis.add(new StackWrapper(s, num));
	}

	private void addToList(List<FluidStack> lis, Fluid s, int num) {
		boolean added = false;
		for (int i = 0; i < lis.size(); i++) {
			FluidStack stack = lis.get(i);
			if (stack.getFluid() == s) {
				lis.get(i).amount += num;
				added = true;
			}
		}
		if (!added)
			lis.add(new FluidStack(s, num));
	}

	public int getAmount(FilterItem fil) {
		if (fil == null)
			return 0;
		int size = 0;
		ItemStack s = fil.getStack();
		for (StackWrapper w : getStacks()) {
			if (!fil.isOre()) {
				if (fil.isMeta() ? w.getStack().isItemEqual(s) : w.getStack().getItem() == s.getItem())
					size += w.getSize();
			} else {
				if (Util.equalOreDict(w.getStack(), s))
					size += w.getSize();
			}
		}
		return size;
	}

	public int getAmount(Fluid fluid) {
		if (fluid == null)
			return 0;
		int size = 0;
		for (FluidStack w : getFluids()) {
			if (w.getFluid() == fluid)
				size += w.amount;
		}
		return size;
	}

	public List<ItemStack> getTemplates(FilterItem fil, boolean nbt) {
		ItemStack stack = fil.getStack();
		boolean meta = fil.isMeta(), ore = fil.isOre();
		List<ItemStack> templates = new ArrayList<ItemStack>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileContainer))
				continue;
			TileContainer tile = (TileContainer) worldObj.getTileEntity(p);
			for (ItemStack s : tile.getTemplates()) {
				NBTTagCompound res = (NBTTagCompound) s.getTagCompound().getTag("res");
				ItemStack result = ItemStack.loadItemStackFromNBT(res);
				if (!ore) {
					if (!meta ? result.getItem() == stack.getItem() : result.isItemEqual(stack) && (!nbt || ItemStack.areItemStackTagsEqual(result, stack))) {
						ItemStack a = s;
						a.stackSize = result.stackSize;
						templates.add(s);
					}
				} else {
					if (Util.equalOreDict(stack, result)) {
						ItemStack a = s;
						a.stackSize = result.stackSize;
						templates.add(s);
					}
				}
			}
		}
		return templates;
	}

	private List<FilterItem> getIngredients(ItemStack template) {
		Map<Integer, ItemStack> stacks = Maps.<Integer, ItemStack> newHashMap();
		Map<Integer, Boolean> metas = Maps.<Integer, Boolean> newHashMap();
		Map<Integer, Boolean> ores = Maps.<Integer, Boolean> newHashMap();
		NBTTagList invList = template.getTagCompound().getTagList("crunchItem", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			stacks.put(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}
		List<FilterItem> list = new ArrayList<FilterItem>();
		for (int i = 1; i < 10; i++) {
			metas.put(i - 1, NBTHelper.getBoolean(template, "meta" + i));
			ores.put(i - 1, NBTHelper.getBoolean(template, "ore" + i));
		}
		for (Entry<Integer, ItemStack> e : stacks.entrySet()) {
			if (e.getValue() != null) {
				boolean meta = metas.get(e.getKey()), ore = ores.get(e.getKey());
				list.add(new FilterItem(e.getValue(), meta, ore));
			}
		}
		return list;

	}

	public int canCraft(List<StackWrapper> stacks, FilterItem fil, int num, boolean neww) {
		int result = 0;
		for (int ii = 0; ii < getTemplates(fil, false).size(); ii++) {
			ItemStack s = getTemplates(fil, false).get(ii);
			if (neww)
				stacks = getStacks();
			boolean done = true;
			int con = num / s.stackSize;
			if (num % s.stackSize != 0)
				con++;
			for (int i = 0; i < con; i++) {
				boolean oneCraft = true;
				for (FilterItem f : getIngredients(s)) {
					if (!oneCraft)
						break;
					while (true) {
						boolean found = consume(stacks, f, 1) == 1;
						if (!found) {
							int t = canCraft(stacks, f, 1, false);
							if (t != 0) {
								addToList(stacks, f.getStack(), t);

							} else {
								oneCraft = false;
								break;
							}
						} else {
							break;
						}
					}
				}
				if (oneCraft)
					result += s.stackSize;
			}

		}
		return result;
	}

	public int getMissing(List<StackWrapper> stacks, FilterItem fil, int num, boolean neww, List<FilterItem> missing) {
		int result = 0;
		for (int ii = 0; ii < getTemplates(fil, false).size(); ii++) {
			ItemStack s = getTemplates(fil, false).get(ii);
			if (neww)
				stacks = getStacks();
			boolean done = true;
			int con = num / s.stackSize;
			if (num % s.stackSize != 0)
				con++;
			for (int i = 0; i < con; i++) {
				boolean oneCraft = true;
				for (FilterItem f : getIngredients(s)) {
					// if (!oneCraft)
					// break;
					while (true) {
						boolean found = consume(stacks, f, 1) == 1;
						System.out.println("found: " + found + ", " + f);
						if (!found) {
							int t = getMissing(stacks, f, 1, false, missing);
							if (t != 0) {
								addToList(stacks, f.getStack(), t);

							} else {
								oneCraft = false;
								missing.add(f);
								break;
							}
						} else {
							break;
						}
					}
				}
				if (oneCraft)
					result += s.stackSize;
			}

		}
		return result;
	}

	public ItemStack craft(FilterItem fil, int num) {
		for (ItemStack s : getTemplates(fil, false)) {
			boolean done = false;
			ItemStack last = null;
			for (FilterItem f : getIngredients(s)) {
				int con = num / s.stackSize;
				if (num % s.stackSize != 0)
					con++;
				// System.out.println("consume: "+con+" "+f.getStack());
				ItemStack req = request(f.getStack(), con, f.isMeta(), false, f.isOre(), false);
				if (req != null && req.stackSize == con) {
					done = true;
					continue;
				}
				int rest = con - req.stackSize;
				// System.out.println("craft: "+con+" "+f.getStack());
				ItemStack craft = craft(f, rest);
				if (craft != null && craft.stackSize == rest) {
					done = true;
					continue;
				}
				done = false;
				break;
			}
			if (done)
				return null;

		}
		return null;
	}

	private int consume(List<StackWrapper> wraps, FilterItem fil, int num) {
		boolean meta = fil.isMeta(), ore = fil.isOre();
		ItemStack stack = fil.getStack();
		// System.out.println(fil.getStack()+" "+num);
		int rest = num;
		for (StackWrapper w : wraps) {
			if (!ore) {
				if (meta ? w.getStack().isItemEqual(stack) : w.getStack().getItem() == stack.getItem()) {
					if (w.getSize() >= rest) {
						w.setSize(w.getSize() - rest);
						if (w.getSize() == 0) {
							// w = null;
							wraps.remove(w);
							// wraps.removeAll(Collections.singleton(null));
						}
						return num;
					} else {
						rest = rest - w.getSize();
						// w = null;
						wraps.remove(w);
						// wraps.removeAll(Collections.singleton(null));
					}
				}
			} else {
				if (Util.equalOreDict(w.getStack(), stack)) {
					if (w.getSize() >= rest) {
						w.setSize(w.getSize() - rest);
						if (w.getSize() == 0) {
							// w = null;
							wraps.remove(w);
							// wraps.removeAll(Collections.singleton(null));
						}
						return num;
					} else {
						rest = rest - w.getSize();
						// w = null;
						wraps.remove(w);
						// wraps.removeAll(Collections.singleton(null));
					}
				}
			}
		}
		return num - rest;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		connectables = new Gson().fromJson(compound.getString("cables"), new TypeToken<List<BlockPos>>() {
		}.getType());
		storageInventorys = new Gson().fromJson(compound.getString("storageInventorys"), new TypeToken<List<BlockPos>>() {
		}.getType());
		imInventorys = new Gson().fromJson(compound.getString("imInventorys"), new TypeToken<List<BlockPos>>() {
		}.getType());
		exInventorys = new Gson().fromJson(compound.getString("exInventorys"), new TypeToken<List<BlockPos>>() {
		}.getType());
		en.readFromNBT(compound);
		NBTTagList tasksList = compound.getTagList("tasks", Constants.NBT.TAG_COMPOUND);
		tasks = new ArrayList<CraftingTask>();
		for (int i = 0; i < tasksList.tagCount(); i++) {
			NBTTagCompound stackTag = tasksList.getCompoundTagAt(i);
			CraftingTask t = new CraftingTask();
			t.readFromNBT(stackTag);
			tasks.add(t);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("cables", new Gson().toJson(connectables));
		compound.setString("storageInventorys", new Gson().toJson(storageInventorys));
		compound.setString("imInventorys", new Gson().toJson(imInventorys));
		compound.setString("exInventorys", new Gson().toJson(exInventorys));
		en.writeToNBT(compound);
		NBTTagList tasksList = new NBTTagList();
		for (CraftingTask t : tasks) {
			NBTTagCompound stackTag = new NBTTagCompound();
			t.writeToNBT(stackTag);
			tasksList.appendTag(stackTag);
		}
		compound.setTag("tasks", tasksList);
	}

	private void addCables(BlockPos pos, int num) {
		if (connectables == null)
			connectables = new ArrayList<BlockPos>();
		for (BlockPos bl : Util.getSides(pos)) {
			if (worldObj.getBlockState(bl).getBlock() == ModBlocks.master && !bl.equals(this.pos) && worldObj.getChunkFromBlockCoords(bl) != null && worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				worldObj.getBlockState(bl).getBlock().dropBlockAsItem(worldObj, bl, worldObj.getBlockState(bl), 0);
				worldObj.setBlockToAir(bl);
				continue;
			}
			if (worldObj.getTileEntity(bl) instanceof IConnectable && !connectables.contains(bl) && worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				connectables.add(bl);
				((IConnectable) worldObj.getTileEntity(bl)).setMaster(this.pos);
				addCables(bl, num++);
			}
		}
	}

	private void addInventorys() {
		storageInventorys = new ArrayList<BlockPos>();
		imInventorys = new ArrayList<BlockPos>();
		exInventorys = new ArrayList<BlockPos>();
		fstorageInventorys = new ArrayList<BlockPos>();
		fimInventorys = new ArrayList<BlockPos>();
		fexInventorys = new ArrayList<BlockPos>();
		for (BlockPos cable : connectables) {
			if (!(worldObj.getTileEntity(cable) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(cable);
			if (tile.getKind() == Kind.exKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null && worldObj.getTileEntity(cable.offset(face)) instanceof IInventory && worldObj.getChunkFromBlockCoords(cable.offset(face)).isLoaded())
					exInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.imKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null && worldObj.getTileEntity(cable.offset(face)) instanceof IInventory && worldObj.getChunkFromBlockCoords(cable.offset(face)).isLoaded())
					imInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.storageKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null && worldObj.getTileEntity(cable.offset(face)) instanceof IInventory && worldObj.getChunkFromBlockCoords(cable.offset(face)).isLoaded())
					storageInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.fexKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null && worldObj.getTileEntity(cable.offset(face)) instanceof IFluidHandler && worldObj.getChunkFromBlockCoords(cable.offset(face)).isLoaded())
					fexInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.fimKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null && worldObj.getTileEntity(cable.offset(face)) instanceof IFluidHandler && worldObj.getChunkFromBlockCoords(cable.offset(face)).isLoaded())
					fimInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.fstorageKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null && worldObj.getTileEntity(cable.offset(face)) instanceof IFluidHandler && worldObj.getChunkFromBlockCoords(cable.offset(face)).isLoaded())
					fstorageInventorys.add(cable.offset(face));
			}
		}
	}

	public void refreshNetwork() {
		connectables = null;
		addCables(pos, 0);
		Collections.sort(connectables, new Comparator<BlockPos>() {
			@Override
			public int compare(BlockPos o1, BlockPos o2) {
				double dis1 = o1.distanceSq(pos);
				double dis2 = o2.distanceSq(pos);
				return Double.compare(dis1, dis2);
			}
		});
		addInventorys();
	}

	public void vacuum() {
		if ((worldObj.getTotalWorldTime() + 0) % 30 != 0)
			return;
		if (connectables == null)
			refreshNetwork();
		for (BlockPos p : connectables) {
			if (worldObj.getTileEntity(p) != null && worldObj.getTileEntity(p) instanceof TileKabel && ((TileKabel) worldObj.getTileEntity(p)).getKind() == Kind.vacuumKabel) {
				int range = 2;

				int x = p.getX();
				int y = p.getY();
				int z = p.getZ();

				List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.fromBounds(x - range, y - range, z - range, x + range + 1, y + range + 1, z + range + 1));
				for (EntityItem item : items) {
					if (item.ticksExisted < 40 || item.isDead || !consumeRF(item.getEntityItem().stackSize, false))
						continue;
					ItemStack stack = item.getEntityItem().copy();
					if (!worldObj.isRemote) {
						int rest = insertStack(stack, null);
						ItemStack r = stack.copy();
						r.stackSize = rest;
						if (rest <= 0)
							item.setDead();
						else
							item.setEntityItemStack(r);
						break;
					}
				}
			}
		}
	}

	public int insertStack(ItemStack stack, IInventory source) {
		if (stack == null)
			return 0;
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.storageKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IInventory) {
				invs.add(tile);
			}
		}
		Collections.sort(invs, new Comparator<TileKabel>() {
			@Override
			public int compare(TileKabel o1, TileKabel o2) {
				return Integer.compare(o2.getPriority(), o1.getPriority());
			}
		});

		return addToInventories(stack, invs, source);
	}

	int addToInventories(ItemStack stack, List<TileKabel> list, IInventory source) {
		ItemStack in = stack.copy();
		for (TileKabel t : list) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if (inv instanceof ISidedInventory && !Inv.contains((ISidedInventory) inv, in, t.getInventoryFace()))
				continue;
			if (!(inv instanceof ISidedInventory) && !Inv.contains(inv, in))
				continue;
			if (!t.canTransfer(stack))
				continue;
			if (Inv.isInventorySame((TileEntity) inv, (TileEntity) source))
				continue;
			int remain = (inv instanceof ISidedInventory) ? Inv.addToSidedInventoryWithLeftover(in, (ISidedInventory) inv, t.getInventoryFace().getOpposite(), false) : Inv.addToInventoryWithLeftover(in, inv, false);
			if (remain == 0)
				return 0;
			in = Inv.copyStack(in, remain);
			inv.markDirty();
		}
		for (TileKabel t : list) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if (inv instanceof ISidedInventory && Inv.contains((ISidedInventory) inv, in, t.getInventoryFace()))
				continue;
			if (!(inv instanceof ISidedInventory) && Inv.contains(inv, in))
				continue;
			if (!t.canTransfer(stack))
				continue;
			if (Inv.isInventorySame((TileEntity) inv, (TileEntity) source))
				continue;
			int remain = (inv instanceof ISidedInventory) ? Inv.addToSidedInventoryWithLeftover(in, (ISidedInventory) inv, t.getInventoryFace().getOpposite(), false) : Inv.addToInventoryWithLeftover(in, inv, false);
			if (remain == 0)
				return 0;
			in = Inv.copyStack(in, remain);
			inv.markDirty();
		}
		return in.stackSize;
	}

	public int insertFluid(FluidStack stack, IFluidHandler source) {
		if (stack == null)
			return 0;
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.fstorageKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IFluidHandler) {
				invs.add(tile);
			}
		}
		Collections.sort(invs, new Comparator<TileKabel>() {
			@Override
			public int compare(TileKabel o1, TileKabel o2) {
				return Integer.compare(o2.getPriority(), o1.getPriority());
			}
		});

		return addToTanks(stack, invs, source);
	}

	int addToTanks(FluidStack stack, List<TileKabel> list, IFluidHandler source) {
		FluidStack in = stack;
		for (TileKabel t : list) {
			IFluidHandler inv = (IFluidHandler) worldObj.getTileEntity(t.getConnectedInventory());
			if (!Inv.contains(inv, in.getFluid(), t.getInventoryFace()))
				continue;
			if (!t.canTransfer(stack.getFluid()))
				continue;
			if (Inv.isInventorySame((TileEntity) inv, (TileEntity) source))
				continue;
			int remain = in.amount - inv.fill(t.getInventoryFace().getOpposite(), in, true);
			if (remain <= 0)
				return 0;
			in = new FluidStack(in.getFluid(), remain);
		}
		for (TileKabel t : list) {
			IFluidHandler inv = (IFluidHandler) worldObj.getTileEntity(t.getConnectedInventory());
			if (Inv.contains(inv, in.getFluid(), t.getInventoryFace()))
				continue;
			if (!t.canTransfer(stack.getFluid()))
				continue;
			if (Inv.isInventorySame((TileEntity) inv, (TileEntity) source))
				continue;
			int remain = in.amount - inv.fill(t.getInventoryFace().getOpposite(), in, true);
			if (remain <= 0)
				return 0;
			in = new FluidStack(in.getFluid(), remain);
		}
		return in.amount;
	}

	public void impor() {
		if (imInventorys == null || storageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.imKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IInventory) {
				invs.add(tile);
			}
		}
		Collections.sort(invs, new Comparator<TileKabel>() {
			@Override
			public int compare(TileKabel o1, TileKabel o2) {
				return Integer.compare(o2.getPriority(), o1.getPriority());
			}
		});
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if ((worldObj.getTotalWorldTime() + 10) % (30 / (t.elements(ItemUpgrade.SPEED) + 1)) != 0)
				continue;
			if (!(inv instanceof ISidedInventory)) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!t.canTransfer(s))
						continue;
					if (!t.status())
						continue;
					int num = s.stackSize;
					int insert = Math.min(s.stackSize, (int) Math.pow(2, t.elements(ItemUpgrade.STACK) + 2));
					if (!consumeRF(insert + t.elements(ItemUpgrade.SPEED), false))
						continue;
					int rest = insertStack(Inv.copyStack(s, insert), inv);
					if (insert == rest)
						continue;
					inv.setInventorySlotContents(i, rest > 0 ? Inv.copyStack(s.copy(), (num - insert) + rest) : Inv.copyStack(s.copy(), num - insert));
					inv.markDirty();
					break;

				}
			} else {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t.getInventoryFace().getOpposite())) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!t.canTransfer(s))
						continue;
					if (!t.status())
						continue;
					if (!((ISidedInventory) inv).canExtractItem(i, s, t.getInventoryFace().getOpposite()))
						continue;
					int num = s.stackSize;
					int insert = Math.min(s.stackSize, (int) Math.pow(2, t.elements(ItemUpgrade.STACK) + 2));
					if (!consumeRF(insert + t.elements(ItemUpgrade.SPEED), false))
						continue;
					int rest = insertStack(Inv.copyStack(s, insert), inv);
					if (insert == rest)
						continue;
					inv.setInventorySlotContents(i, rest > 0 ? Inv.copyStack(s.copy(), (num - insert) + rest) : Inv.copyStack(s.copy(), num - insert));

					inv.markDirty();
					break;
				}
			}
		}
	}

	public void fimpor() {
		if (fimInventorys == null || fstorageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.fimKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IFluidHandler) {
				invs.add(tile);
			}
		}
		Collections.sort(invs, new Comparator<TileKabel>() {
			@Override
			public int compare(TileKabel o1, TileKabel o2) {
				return Integer.compare(o2.getPriority(), o1.getPriority());
			}
		});
		for (TileKabel t : invs) {
			IFluidHandler inv = (IFluidHandler) worldObj.getTileEntity(t.getConnectedInventory());
			if ((worldObj.getTotalWorldTime() + 10) % (30 / (t.elements(ItemUpgrade.SPEED) + 1)) != 0)
				continue;
			if (inv.getTankInfo(t.getInventoryFace().getOpposite()) == null)
				continue;
//			System.out.println("inv: " + inv);
//			System.out.println("inv info: " + inv.getTankInfo(t.getInventoryFace().getOpposite()));
			for (FluidTankInfo i : inv.getTankInfo(t.getInventoryFace().getOpposite())) {
				FluidStack s = i.fluid;
				if (s == null)
					continue;
				if (!t.canTransfer(s.getFluid()))
					continue;
				if (!t.status())
					continue;
				if (!inv.canDrain(t.getInventoryFace().getOpposite(), s.getFluid()))
					continue;
				int num = s.amount;
				int insert = Math.min(s.amount, 200 + t.elements(ItemUpgrade.STACK) * 200);
				if (!consumeRF(insert + t.elements(ItemUpgrade.SPEED), false))
					continue;
				int rest = insertFluid(new FluidStack(s, insert), inv);
				if (insert == rest)
					continue;
				inv.drain(t.getInventoryFace().getOpposite(), new FluidStack(s.getFluid(), insert - rest), true);
				break;

			}
		}
	}

	public void export() {
		if (exInventorys == null || storageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.exKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IInventory) {
				invs.add(tile);
			}
		}
		Collections.sort(invs, new Comparator<TileKabel>() {
			@Override
			public int compare(TileKabel o1, TileKabel o2) {
				return Integer.compare(o1.getPriority(), o2.getPriority());
			}
		});
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if ((worldObj.getTotalWorldTime() + 20) % (30 / (t.elements(ItemUpgrade.SPEED) + 1)) != 0)
				continue;
			for (int i = 0; i < 9; i++) {
				if (t.getFilter().get(i) == null)
					continue;
				boolean ore = t.getOres().get(i) == null ? false : t.getOres().get(i);
				ItemStack fil = t.getFilter().get(i).getStack();

				if (fil == null)
					continue;
				if (storageInventorys.contains(t.getPos()))
					continue;
				ItemStack g = request(fil, 1, t.getMetas().get(i), false, ore, true);
				if (g == null)
					continue;
				int space = Math.min(Inv.getSpace(g, inv, t.getInventoryFace().getOpposite()), (t.elements(ItemUpgrade.STOCK) < 1) ? Integer.MAX_VALUE : t.getFilter().get(i).getSize() - Inv.getAmount(g, inv, t.getInventoryFace().getOpposite(), t.getMetas().get(i), ore));
				if (space <= 0)
					continue;
				if (!t.status())
					continue;
				int num = Math.min(Math.min(g.getMaxStackSize(), inv.getInventoryStackLimit()), Math.min(space, (int) Math.pow(2, t.elements(ItemUpgrade.STACK) + 2)));
				if (!consumeRF(num + t.elements(ItemUpgrade.SPEED), true))
					continue;
				ItemStack rec = request(g, num, true, false, false, false);
				if (rec == null)
					continue;
				consumeRF(rec.stackSize + t.elements(ItemUpgrade.SPEED), false);

				TileEntityHopper.putStackInInventoryAllSlots(inv, rec, t.getInventoryFace().getOpposite());
				break;
			}
		}
	}

	public void fexport() {
		if (fexInventorys == null || fstorageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile.getKind() == Kind.fexKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IFluidHandler) {
				invs.add(tile);
			}
		}
		Collections.sort(invs, new Comparator<TileKabel>() {
			@Override
			public int compare(TileKabel o1, TileKabel o2) {
				return Integer.compare(o1.getPriority(), o2.getPriority());
			}
		});
		for (TileKabel t : invs) {
			IFluidHandler inv = (IFluidHandler) worldObj.getTileEntity(t.getConnectedInventory());
			if ((worldObj.getTotalWorldTime() + 20) % (30 / (t.elements(ItemUpgrade.SPEED) + 1)) != 0)
				continue;
			for (int i = 0; i < 9; i++) {
				if (t.getFilter().get(i) == null)
					continue;
				ItemStack fil = t.getFilter().get(i).getStack();
				if (fil == null)
					continue;
				// Fluid f =
				// FluidRegistry.lookupFluidForBlock(Block.getBlockFromItem(fil.getItem()));
				FluidStack fs = Util.getFluid(fil);
				if (fs == null || fs.getFluid() == null)
					continue;
				Fluid f = fs.getFluid();
				if (fstorageInventorys.contains(t.getPos()))
					continue;
				if (!inv.canFill(t.getInventoryFace().getOpposite(), f))
					continue;
				// int amount = 0;
				// for (FluidTankInfo inf :
				// inv.getTankInfo(t.getInventoryFace().getOpposite()))
				// if (inf.fluid != null && inf.fluid.getFluid() == f)
				// amount += inf.fluid.amount;
				if (!t.status())
					continue;
				int num = 200 + t.elements(ItemUpgrade.STACK) * 200;
				FluidStack recs = frequest(f, num, true);
				if (recs == null)
					continue;
				if (inv.fill(t.getInventoryFace().getOpposite(), recs, false) <= 0)
					continue;
				if (!consumeRF(num + t.elements(ItemUpgrade.SPEED), true))
					continue;
				FluidStack rec = frequest(f, num, false);
				if (rec == null)
					continue;
				consumeRF(rec.amount + t.elements(ItemUpgrade.SPEED), false);
				inv.fill(t.getInventoryFace().getOpposite(), rec, true);
				break;
			}
		}
	}

	public ItemStack request(ItemStack stack, final int size, boolean meta, boolean tag, boolean ore, boolean simulate) {
		if (size == 0 || stack == null)
			return null;
		if (storageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				continue;
			}
			if (tile.getKind() == Kind.storageKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IInventory) {
				invs.add(tile);
			}
		}
		ItemStack res = null;
		int result = 0;
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if (!(inv instanceof ISidedInventory)) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (res != null && !s.isItemEqual(res))
						continue;
					if (!ore) {
						if (!ItemStack.areItemStackTagsEqual(s, stack) && tag)
							continue;
						if (!s.isItemEqual(stack) && meta)
							continue;
						if (s.getItem() != stack.getItem() && !meta)
							continue;
					} else {
						if (!Util.equalOreDict(s, stack))
							continue;
					}
					if (!t.canTransfer(s))
						continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
					if (!simulate)
						inv.setInventorySlotContents(i, rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					if (res == null)
						res = s.copy();
					inv.markDirty();
					if (result == size)
						return Inv.copyStack(res, size);
					// break;
				}
			} else {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t.getInventoryFace().getOpposite())) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (res != null && !s.isItemEqual(res))
						continue;
					if (!ore) {
						if (!ItemStack.areItemStackTagsEqual(s, stack) && tag)
							continue;
						if (!s.isItemEqual(stack) && meta)
							continue;
						if (s.getItem() != stack.getItem() && !meta)
							continue;
					} else {
						if (!Util.equalOreDict(s, stack))
							continue;
					}
					if (!t.canTransfer(s))
						continue;
					if (!((ISidedInventory) inv).canExtractItem(i, s, t.getInventoryFace().getOpposite()))
						continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
					if (!simulate)
						inv.setInventorySlotContents(i, rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					if (res == null)
						res = s.copy();
					inv.markDirty();
					if (result == size)
						return Inv.copyStack(res, size);
					// break;
				}
			}
		}
		if (result == 0)
			return null;
		return Inv.copyStack(res, result);
	}

	public FluidStack frequest(Fluid fluid, final int size, boolean simulate) {
		if (size == 0 || fluid == null)
			return null;
		if (fstorageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				continue;
			}
			if (tile.getKind() == Kind.fstorageKabel && tile.getConnectedInventory() != null && worldObj.getTileEntity(tile.getConnectedInventory()) instanceof IFluidHandler) {
				invs.add(tile);
			}
		}
		FluidStack res = null;
		int result = 0;
		for (TileKabel t : invs) {
			IFluidHandler inv = (IFluidHandler) worldObj.getTileEntity(t.getConnectedInventory());
			if (inv.getTankInfo(t.getInventoryFace().getOpposite()) == null)
				continue;
			for (FluidTankInfo i : inv.getTankInfo(t.getInventoryFace().getOpposite())) {
				FluidStack s = i.fluid;
				if (s == null)
					continue;
				if (res != null && s.getFluid() != res.getFluid())
					continue;
				if (s.getFluid() != fluid)
					continue;
				if (!t.canTransfer(fluid))
					continue;
				if (!inv.canDrain(t.getInventoryFace().getOpposite(), fluid))
					continue;
				int miss = size - result;
				result += Math.min(s.amount, miss);
				int rest = s.amount - miss;
				if (!simulate)
					inv.drain(t.getInventoryFace().getOpposite(), new FluidStack(s.getFluid(), miss), true);
				if (res == null)
					res = s.copy();
				if (result == size)
					return new FluidStack(res.getFluid(), size);
				// break;

			}
		}
		if (result == 0)
			return null;
		return new FluidStack(res.getFluid(), result);
	}

	public void craft() {

	}

	@Override
	public void update() {
		if (connectables != null)
			for (BlockPos p : connectables)
				if (!(worldObj.getTileEntity(p) instanceof IConnectable)) {
					refreshNetwork();
					break;
				}
		if (worldObj.getTotalWorldTime() % 200 == 0)
			refreshNetwork();
		vacuum();
		impor();
		export();
		// craft();
		if (!worldObj.isRemote) {
			fimpor();
			fexport();
		}
	}

	boolean consumeRF(int num, boolean simulate) {
		if (!ConfigHandler.energyNeeded)
			return true;
		int value = num * ConfigHandler.energyMultiplier + connectables.size();
		if (en.getEnergyStored() < value)
			return false;
		if (!simulate) {
			en.modifyEnergyStored(-value);
		}
		return true;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return en.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return en.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return en.receiveEnergy(maxReceive, simulate);
	}

}
