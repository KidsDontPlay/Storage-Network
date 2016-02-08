package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

public class TileMaster extends TileEntity implements ITickable, IFluidHandler {
	public List<BlockPos> connectables, storageInventorys, imInventorys, exInventorys;
	public FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * ConfigHandler.lavaCapacity);

	public List<StackWrapper> getStacks() {
		List<StackWrapper> stacks = new ArrayList<StackWrapper>();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : connectables) {
			if (!(worldObj.getTileEntity(p) instanceof TileKabel))
				continue;
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				return stacks;
			}
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
					if (stack != null && stack.getItem() != null) {
						addToList(stacks, stack, drawer.getStoredItemCount());
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

	public int getAmount(ItemStack s) {
		for (StackWrapper w : getStacks()) {
			if (w.getStack().isItemEqual(s))
				return w.getSize();
		}
		return 0;
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
		tank.readFromNBT(compound);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("cables", new Gson().toJson(connectables));
		compound.setString("storageInventorys", new Gson().toJson(storageInventorys));
		compound.setString("imInventorys", new Gson().toJson(imInventorys));
		compound.setString("exInventorys", new Gson().toJson(exInventorys));
		tank.writeToNBT(compound);
	}

	private void addCables(BlockPos pos, int num) {
		if (connectables == null)
			connectables = new ArrayList<BlockPos>();
		for (BlockPos bl : getSides(pos)) {
			if (worldObj.getBlockState(bl).getBlock() == ModBlocks.master && !bl.equals(this.pos) && worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				worldObj.getBlockState(bl).getBlock().dropBlockAsItem(worldObj, bl, worldObj.getBlockState(bl), 0);
				worldObj.setBlockToAir(bl);
				continue;
			}
			if (worldObj.getTileEntity(bl) instanceof IConnectable && !connectables.contains(bl) && worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				connectables.add(bl);
				((IConnectable) worldObj.getTileEntity(bl)).setMaster(this.pos);
				worldObj.markBlockForUpdate(bl);
				addCables(bl, num++);
			}
		}
	}

	private void addInventorys() {
		storageInventorys = new ArrayList<BlockPos>();
		imInventorys = new ArrayList<BlockPos>();
		exInventorys = new ArrayList<BlockPos>();
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
			}
		}
	}

	public static List<BlockPos> getSides(BlockPos pos) {
		List<BlockPos> lis = new ArrayList<BlockPos>();
		lis.add(pos.up());
		lis.add(pos.down());
		lis.add(pos.east());
		lis.add(pos.west());
		lis.add(pos.north());
		lis.add(pos.south());
		return lis;
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
					if (item.ticksExisted < 40 || item.isDead || !consumeLava(item.getEntityItem().stackSize, false))
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
		if (stack == null)
			return 0;
		ItemStack in = stack.copy();
		for (TileKabel t : list) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t.getConnectedInventory());
			if (inv instanceof ISidedInventory && !Inv.contains((ISidedInventory) inv, in, t.getInventoryFace()))
				continue;
			if (!(inv instanceof ISidedInventory) && !Inv.contains(inv, in))
				continue;
			if (!t.canTransfer(stack))
				continue;
			if (Inv.isInventorySame(inv, source))
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
			if (Inv.isInventorySame(inv, source))
				continue;
			int remain = (inv instanceof ISidedInventory) ? Inv.addToSidedInventoryWithLeftover(in, (ISidedInventory) inv, t.getInventoryFace().getOpposite(), false) : Inv.addToInventoryWithLeftover(in, inv, false);
			if (remain == 0)
				return 0;
			in = Inv.copyStack(in, remain);
			inv.markDirty();
		}
		return in.stackSize;
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
			if ((worldObj.getTotalWorldTime() + 10) % (30 / (t.elements(0) + 1)) != 0)
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
					int insert = Math.min(s.stackSize, (int) Math.pow(2, t.elements(2) + 2));
					if (!consumeLava(insert + t.elements(0) / 2, false))
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
					int insert = Math.min(s.stackSize, (int) Math.pow(2, t.elements(2) + 2));
					if (!consumeLava(insert + t.elements(0) / 2, false))
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
			if ((worldObj.getTotalWorldTime() + 20) % (30 / (t.elements(0) + 1)) != 0)
				continue;
			for (int i = 0; i < 9; i++) {
				ItemStack fil = t.getFilter().get(i);

				if (fil == null)
					continue;
				if (storageInventorys.contains(t.getPos()))
					continue;
				int space = getSpace(fil, inv, t.getInventoryFace().getOpposite());
				if (space == 0)
					continue;
				if (!t.status())
					continue;
				int num = Math.min(Math.min(fil.getMaxStackSize(), inv.getInventoryStackLimit()), Math.min(space, (int) Math.pow(2, t.elements(2) + 2)));
				if (!consumeLava(num + t.elements(0) / 2, true))
					continue;
				ItemStack rec = request(fil, num, t.isMeta(), false);
				if (rec == null)
					continue;
				consumeLava(rec.stackSize + t.elements(0) / 2, false);

				TileEntityHopper.putStackInInventoryAllSlots(inv, rec, t.getInventoryFace().getOpposite());
				break;
			}
		}
	}

	private int getSpace(ItemStack fil, IInventory inv, EnumFacing face) {
		int space = 0;
		if (!(inv instanceof ISidedInventory)) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				if (!inv.isItemValidForSlot(i, fil))
					continue;
				ItemStack slot = inv.getStackInSlot(i);
				int max = Math.min(fil.getMaxStackSize(), inv.getInventoryStackLimit());
				if (slot == null) {
					space += max;
				} else {
					if (slot.isItemEqual(fil) && ItemStack.areItemStackTagsEqual(fil, slot)) {
						space += max - slot.stackSize;
					}
				}
			}
		} else {
			for (int i : ((ISidedInventory) inv).getSlotsForFace(face)) {
				if (!inv.isItemValidForSlot(i, fil) || !((ISidedInventory) inv).canInsertItem(i, fil, face))
					continue;
				ItemStack slot = inv.getStackInSlot(i);
				int max = Math.min(fil.getMaxStackSize(), inv.getInventoryStackLimit());
				if (slot == null) {
					space += max;
				} else {
					if (slot.isItemEqual(fil) && ItemStack.areItemStackTagsEqual(fil, slot)) {
						space += max - slot.stackSize;
					}
				}
			}
		}
		return space;
	}

	public ItemStack request(ItemStack stack, final int size, boolean meta, boolean tag) {
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
			if (tile.getKind() == Kind.storageKabel && tile.getConnectedInventory() != null) {
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
					if (!ItemStack.areItemStackTagsEqual(s, stack) && tag)
						continue;
					if (!s.isItemEqual(stack) && meta)
						continue;
					if (s.getItem() != stack.getItem() && !meta)
						continue;
					if (!t.canTransfer(s))
						continue;
					// if (!t.status())
					// continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
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
					if (!ItemStack.areItemStackTagsEqual(s, stack) && tag)
						continue;
					if (!s.isItemEqual(stack) && meta)
						continue;
					if (s.getItem() != stack.getItem() && !meta)
						continue;
					if (!t.canTransfer(s))
						continue;
					// if (!t.status())
					// continue;
					if (!((ISidedInventory) inv).canExtractItem(i, s, t.getInventoryFace().getOpposite()))
						continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
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

	@Override
	public void update() {
		if (connectables != null)
			for (BlockPos p : connectables)
				if (!(worldObj.getTileEntity(p) instanceof IConnectable)) {
					refreshNetwork();
					break;
				}
		vacuum();
		impor();
		export();
		// if (worldObj.getTotalWorldTime() % 40 == 0)
		// System.out.println("tick");

	}

	boolean consumeLava(int num, boolean simulate) {
		if (!ConfigHandler.lavaNeeded)
			return true;
		if (tank.getFluidAmount() < num * ConfigHandler.energyMultilplier)
			return false;
		if (!simulate)
			tank.drain(num * ConfigHandler.energyMultilplier, true);
		return true;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (resource == null || !canFill(from, resource.getFluid())) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (tank.getFluid() == null || resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return fluid.equals(FluidRegistry.LAVA);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

}
