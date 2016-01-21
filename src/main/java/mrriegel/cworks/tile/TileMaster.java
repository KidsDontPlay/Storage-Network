package mrriegel.cworks.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mrriegel.cworks.blocks.BlockKabel;
import mrriegel.cworks.helper.Inv;
import mrriegel.cworks.init.ModBlocks;
import mrriegel.cworks.tile.TileKabel.Kind;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileMaster extends TileEntity implements ITickable {
	Set<BlockPos> cables, storageInventorys, imInventorys, exInventorys;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		cables = new Gson().fromJson(compound.getString("cables"),
				new TypeToken<Set<BlockPos>>() {
				}.getType());
		storageInventorys = new Gson().fromJson(
				compound.getString("storageInventorys"),
				new TypeToken<Set<BlockPos>>() {
				}.getType());
		imInventorys = new Gson().fromJson(compound.getString("imInventorys"),
				new TypeToken<Set<BlockPos>>() {
				}.getType());
		exInventorys = new Gson().fromJson(compound.getString("exInventorys"),
				new TypeToken<Set<BlockPos>>() {
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
			cables = new HashSet<BlockPos>();
		if (num >= 500) {
			System.out.println("too much cables");
			cables = new HashSet<BlockPos>();
			return;
		}
		for (BlockPos bl : getSides(pos)) {
			if (worldObj.getBlockState(bl).getBlock() == ModBlocks.master
					&& !bl.equals(this.pos)) {
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
		storageInventorys = new HashSet<BlockPos>();
		imInventorys = new HashSet<BlockPos>();
		exInventorys = new HashSet<BlockPos>();
		for (BlockPos cable : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(cable);
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
		addInventorys();
	}

	public void vacuum() {
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
			if (!TileKabel.canInsert(t, stack))
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
			if (!TileKabel.canInsert(t, stack))
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
			inv.markDirty();
		}
		return in.stackSize;
	}

	public void impor() {
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
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			if (!(inv instanceof ISidedInventory)) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					int num = s.stackSize;
					int rest = insertStack(s.copy(), inv);
					if (num == rest)
						continue;
					inv.setInventorySlotContents(i,
							rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
					inv.markDirty();
					break;

				}
			} else {
				for (int i : ((ISidedInventory) inv).getSlotsForFace(t
						.getInventoryFace().getOpposite())) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					if (!((ISidedInventory) inv).canExtractItem(i, s, t
							.getInventoryFace().getOpposite()))
						continue;
					int num = s.stackSize;
					int rest = insertStack(s.copy(), inv);
					if (num == rest)
						continue;
					inv.setInventorySlotContents(i,
							rest > 0 ? Inv.copyStack(s.copy(), rest) : null);
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
		for (TileKabel t : invs) {
			IInventory inv = (IInventory) worldObj.getTileEntity(t
					.getConnectedInventory());
			for (int i = 0; i < 9; i++) {
				ItemStack fil = t.getFilter().get(i);
				if (fil == null)
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
										inv.getInventoryStackLimit()), space));
				if (rec == null)
					continue;

				TileEntityHopper.putStackInInventoryAllSlots(inv, rec, t
						.getInventoryFace().getOpposite());
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

	// boolean isIn(ItemStack stack, List<ItemStack> lis) {
	// for (ItemStack s : lis) {
	// if (s == null || stack == null)
	// continue;
	// if (stack.isItemEqual(s))
	// return true;
	// }
	// return false;
	// }

	public ItemStack request(ItemStack stack, final int size) {
		if (size == 0)
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
					if (s == null || !s.isItemEqual(stack))
						continue;
					int miss = size - result;
					result += Math.min(s.stackSize, miss);
					int rest = s.stackSize - miss;
					System.out.println("auftrag: " + size);
					System.out.println("rest: " + rest);
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
					if (s == null || !s.isItemEqual(stack))
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
		if (worldObj.getTotalWorldTime() % 20 != 0)
			return;
		vacuum();
		impor();
		export();

	}

}
