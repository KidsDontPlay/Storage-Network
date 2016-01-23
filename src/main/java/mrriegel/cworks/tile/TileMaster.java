package mrriegel.cworks.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mrriegel.cworks.blocks.BlockKabel;
import mrriegel.cworks.config.ConfigHandler;
import mrriegel.cworks.helper.Inv;
import mrriegel.cworks.helper.StackWrapper;
import mrriegel.cworks.init.ModBlocks;
import mrriegel.cworks.tile.TileKabel.Kind;
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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileMaster extends TileEntity implements ITickable {
	List<BlockPos> cables, storageInventorys, imInventorys, exInventorys;

	// List<ItemStack> stacks = new ArrayList<ItemStack>();

	public List<StackWrapper> getStacks() {
		List<StackWrapper> stacks = new ArrayList<StackWrapper>();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				return stacks;
			}
			if (tile.getKind() == Kind.storageKabel
					&& tile.getConnectedInventory() != null) {
				invs.add(tile);
			}
		}
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			if (inv == null)
				continue;
			if (inv instanceof ISidedInventory) {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t
						.getInventoryFace().getOpposite())) {
					if (inv.getStackInSlot(i) != null
							&& TileKabel.canTransfer(t, inv.getStackInSlot(i))
							&& ((ISidedInventory) inv).canExtractItem(i, inv
									.getStackInSlot(i), t.getInventoryFace()
									.getOpposite())) {
						addToList(stacks, inv.getStackInSlot(i).copy());
					}
				}
			} else {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					if (inv.getStackInSlot(i) != null
							&& TileKabel.canTransfer(t, inv.getStackInSlot(i)))
						addToList(stacks, inv.getStackInSlot(i).copy());
				}
			}

		}
		return stacks;
	}

	private void addToList(List<StackWrapper> lis, ItemStack s) {
		boolean added = false;
		for (int i = 0; i < lis.size(); i++) {
			ItemStack stack = lis.get(i).getStack();
			if (s.isItemEqual(stack)
					&& ItemStack.areItemStackTagsEqual(stack, s)) {
				lis.get(i).setSize(lis.get(i).getSize() + s.stackSize);
				added = true;
			}
		}
		if (!added)
			lis.add(new StackWrapper(s, s.stackSize));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		cables = new Gson().fromJson(compound.getString("cables"),
				new TypeToken<List<BlockPos>>() {
				}.getType());
		storageInventorys = new Gson().fromJson(
				compound.getString("storageInventorys"),
				new TypeToken<List<BlockPos>>() {
				}.getType());
		imInventorys = new Gson().fromJson(compound.getString("imInventorys"),
				new TypeToken<List<BlockPos>>() {
				}.getType());
		exInventorys = new Gson().fromJson(compound.getString("exInventorys"),
				new TypeToken<List<BlockPos>>() {
				}.getType());
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("cables", new Gson().toJson(cables));
		compound.setString("storageInventorys",
				new Gson().toJson(storageInventorys));
		compound.setString("imInventorys", new Gson().toJson(imInventorys));
		compound.setString("exInventorys", new Gson().toJson(exInventorys));
	}

	private void addCables(BlockPos pos, int num) {
		if (cables == null)
			cables = new ArrayList<BlockPos>();
		if (num >= ConfigHandler.maxCable) {
			System.out.println("too much cables");
			cables = new ArrayList<BlockPos>();
			return;
		}
		for (BlockPos bl : getSides(pos)) {
			if (worldObj.getBlockState(bl).getBlock() == ModBlocks.master
					&& !bl.equals(this.pos)
					&& worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				worldObj.getBlockState(bl)
						.getBlock()
						.dropBlockAsItem(worldObj, bl,
								worldObj.getBlockState(bl), 0);
				worldObj.setBlockToAir(pos);
				continue;
			}
			if (worldObj.getBlockState(bl).getBlock() instanceof BlockKabel
					&& !cables.contains(bl)
					&& worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				cables.add(bl);
				((TileKabel) worldObj.getTileEntity(bl)).setMaster(this.pos);
				addCables(bl, num++);
			}
		}
	}

	private void addInventorys() {
		storageInventorys = new ArrayList<BlockPos>();
		imInventorys = new ArrayList<BlockPos>();
		exInventorys = new ArrayList<BlockPos>();
		for (BlockPos cable : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(cable);
			// for (BlockPos p : getSides(cable)) {
			// if (worldObj.getTileEntity(p) instanceof TileRequest) {
			// ((TileRequest) worldObj.getTileEntity(p))
			// .setMaster(this.pos);
			// }
			// }
			if (tile.getKind() == Kind.exKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null
						&& worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
						&& worldObj.getChunkFromBlockCoords(cable.offset(face))
								.isLoaded())
					exInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.imKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null
						&& worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
						&& worldObj.getChunkFromBlockCoords(cable.offset(face))
								.isLoaded())
					imInventorys.add(cable.offset(face));
			} else if (tile.getKind() == Kind.storageKabel) {
				EnumFacing face = tile.getInventoryFace();
				if (face != null
						&& worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
						&& worldObj.getChunkFromBlockCoords(cable.offset(face))
								.isLoaded())
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
		cables = null;
		addCables(pos, 0);
		Collections.sort(cables, new Comparator<BlockPos>() {
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
		if (cables == null)
			refreshNetwork();
		for (BlockPos p : cables) {
			if (worldObj.getTileEntity(p) != null
					&& ((TileKabel) worldObj.getTileEntity(p)).getKind() == Kind.vacuumKabel) {
				int range = 2;

				int x = p.getX();
				int y = p.getY();
				int z = p.getZ();

				List<EntityItem> items = worldObj.getEntitiesWithinAABB(
						EntityItem.class,
						AxisAlignedBB.fromBounds(x - range, y - range, z
								- range, x + range + 1, y + range + 1, z
								+ range + 1));
				for (EntityItem item : items) {
					if (item.getAge() < 40 || item.isDead)
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
		for (BlockPos p : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				continue;
			}
			if (tile.getKind() == Kind.storageKabel
					&& tile.getConnectedInventory() != null) {
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

	int addToInventories(ItemStack stack, List<TileKabel> list,
			IInventory source) {
		if (stack == null)
			return 0;
		ItemStack in = stack.copy();
		for (TileKabel t : list) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			if (inv instanceof ISidedInventory
					&& !Inv.contains((ISidedInventory) inv, in,
							t.getInventoryFace()))
				continue;
			if (!(inv instanceof ISidedInventory) && !Inv.contains(inv, in))
				continue;
			if (!TileKabel.canTransfer(t, stack))
				continue;
			if (Inv.isInventorySame(inv, source))
				continue;
			int remain = (inv instanceof ISidedInventory) ? Inv
					.addToSidedInventoryWithLeftover(in, (ISidedInventory) inv,
							t.getInventoryFace().getOpposite(), false) : Inv
					.addToInventoryWithLeftover(in, inv, false);
			if (remain == 0)
				return 0;
			in = Inv.copyStack(in, remain);
			// stacks = getStacks();
			inv.markDirty();
		}
		for (TileKabel t : list) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			if (inv instanceof ISidedInventory
					&& Inv.contains((ISidedInventory) inv, in,
							t.getInventoryFace()))
				continue;
			if (!(inv instanceof ISidedInventory) && Inv.contains(inv, in))
				continue;
			if (!TileKabel.canTransfer(t, stack))
				continue;
			if (Inv.isInventorySame(inv, source))
				continue;
			int remain = (inv instanceof ISidedInventory) ? Inv
					.addToSidedInventoryWithLeftover(in, (ISidedInventory) inv,
							t.getInventoryFace().getOpposite(), false) : Inv
					.addToInventoryWithLeftover(in, inv, false);
			// ItemStack re=TileEntityHopper.putStackInInventoryAllSlots(inv,
			// in, t.getInventoryFace().getOpposite());
			if (remain == 0)
				return 0;
			in = Inv.copyStack(in, remain);
			// stacks = getStacks();
			inv.markDirty();
		}
		return in.stackSize;
	}

	public void impor() {
		if ((worldObj.getTotalWorldTime() + 10) % 30 != 0)
			return;
		if (imInventorys == null || storageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				continue;
			}
			if (tile.getKind() == Kind.imKabel
					&& tile.getConnectedInventory() != null) {
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
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			if (!(inv instanceof ISidedInventory)) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!TileKabel.canTransfer(t, s))
						continue;
					// int num = s.stackSize;
					// int rest = insertStack(s.copy(), inv);
					// if (num == rest)
					// continue;
					// inv.setInventorySlotContents(i,
					// rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					int num = s.stackSize;
					int insert = Math.min(s.stackSize, 4);
					int rest = insertStack(Inv.copyStack(s, insert), inv);
					if (insert == rest)
						continue;
					inv.setInventorySlotContents(
							i,
							rest > 0 ? Inv.copyStack(s.copy(), (num - insert)
									+ rest) : Inv.copyStack(s.copy(), num
									- insert));
					inv.markDirty();
					// stacks = getStacks();
					break;

				}
			} else {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t
						.getInventoryFace().getOpposite())) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!TileKabel.canTransfer(t, s))
						continue;
					if (!((ISidedInventory) inv).canExtractItem(i, s, t
							.getInventoryFace().getOpposite()))
						continue;
					// int num = s.stackSize;
					// int rest = insertStack(s.copy(), inv);
					// if (num == rest)
					// continue;
					// inv.setInventorySlotContents(i,
					// rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					int num = s.stackSize;
					int insert = Math.min(s.stackSize, 4);
					int rest = insertStack(Inv.copyStack(s, insert), inv);
					if (insert == rest)
						continue;
					inv.setInventorySlotContents(
							i,
							rest > 0 ? Inv.copyStack(s.copy(), (num - insert)
									+ rest) : Inv.copyStack(s.copy(), num
									- insert));

					inv.markDirty();
					// stacks = getStacks();
					break;
				}
			}
		}
	}

	public void export() {
		if ((worldObj.getTotalWorldTime() + 20) % 30 != 0)
			return;
		if (exInventorys == null || storageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				continue;
			}
			if (tile.getKind() == Kind.exKabel
					&& tile.getConnectedInventory() != null) {
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
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			for (int i = 0; i < 9; i++) {
				ItemStack fil = t.getFilter().get(i);
				if (fil == null)
					continue;
				if (storageInventorys.contains(t.getPos()))
					continue;
				// ItemStack fil = new ItemStack(Blocks.hay_block);
				int space = getSpace(fil, inv, t.getInventoryFace()
						.getOpposite());
				if (space == 0)
					continue;
				ItemStack rec = request(
						fil,
						Math.min(
								Math.min(fil.getMaxStackSize(),
										inv.getInventoryStackLimit()),
								Math.min(space, 4)), t.isMeta(), false);
				if (rec == null)
					continue;

				TileEntityHopper.putStackInInventoryAllSlots(inv, rec, t
						.getInventoryFace().getOpposite());
				// stacks = getStacks();
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
				int max = Math.min(fil.getMaxStackSize(),
						inv.getInventoryStackLimit());
				if (slot == null) {
					space += max;
				} else {
					if (slot.isItemEqual(fil)
							&& ItemStack.areItemStackTagsEqual(fil, slot)) {
						space += max - slot.stackSize;
					}
				}
			}
		} else {
			for (int i : ((ISidedInventory) inv).getSlotsForFace(face)) {
				if (!inv.isItemValidForSlot(i, fil)
						|| !((ISidedInventory) inv).canInsertItem(i, fil, face))
					continue;
				ItemStack slot = inv.getStackInSlot(i);
				int max = Math.min(fil.getMaxStackSize(),
						inv.getInventoryStackLimit());
				if (slot == null) {
					space += max;
				} else {
					if (slot.isItemEqual(fil)
							&& ItemStack.areItemStackTagsEqual(fil, slot)) {
						space += max - slot.stackSize;
					}
				}
			}
		}
		return space;
	}

	public ItemStack request(ItemStack stack, final int size, boolean meta,
			boolean tag) {
		if (size == 0 || stack == null)
			return null;
		if (storageInventorys == null)
			refreshNetwork();
		List<TileKabel> invs = new ArrayList<TileKabel>();
		for (BlockPos p : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(p);
			if (tile == null) {
				refreshNetwork();
				continue;
			}
			if (tile.getKind() == Kind.storageKabel
					&& tile.getConnectedInventory() != null) {
				invs.add(tile);
			}
		}
		int result = 0;
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			if (!(inv instanceof ISidedInventory)) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!ItemStack.areItemStackTagsEqual(s, stack) && tag)
						continue;
					if (!s.isItemEqual(stack) && meta)
						continue;
					if (s.getItem() != stack.getItem() && !meta)
						continue;
					if (!TileKabel.canTransfer(t, s))
						continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
					inv.setInventorySlotContents(i,
							rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					inv.markDirty();
					if (result == size)
						return Inv.copyStack(stack, size);
					break;

				}
			} else {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t
						.getInventoryFace().getOpposite())) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!ItemStack.areItemStackTagsEqual(s, stack) && tag)
						continue;
					if (!s.isItemEqual(stack) && meta)
						continue;
					if (s.getItem() != stack.getItem() && !meta)
						continue;
					if (!TileKabel.canTransfer(t, s))
						continue;
					if (!((ISidedInventory) inv).canExtractItem(i, s, t
							.getInventoryFace().getOpposite()))
						continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
					inv.setInventorySlotContents(i,
							rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					inv.markDirty();
					if (result == size)
						return Inv.copyStack(stack, size);
					break;
				}
			}
		}
		if (result == 0)
			return null;
		return Inv.copyStack(stack, result);
	}

	@Override
	public void update() {
		vacuum();
		impor();
		export();
		// if (worldObj.getTotalWorldTime() % 40 == 0)
		// System.out.println(getStacks());

	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(new BlockPos(this.getPos().getX(),
				this.getPos().getY(), this.getPos().getZ()), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

}
