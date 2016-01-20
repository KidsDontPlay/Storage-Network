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
			return;
		}
		for (BlockPos bl : getSides(pos)) {
			System.out.println("w: " + worldObj);
			System.out.println("n: " + worldObj.getBlockState(bl));
			if (worldObj.getBlockState(bl).getBlock() == ModBlocks.master
					&& !bl.equals(this.pos)) {
				return;
			}
			if (worldObj.getBlockState(bl).getBlock() instanceof BlockKabel
					&& !cables.contains(bl)
					&& worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				cables.add(bl);
				((TileKabel) worldObj.getTileEntity(bl)).setMaster(this.pos);
				addCables(bl, num++);
			}
		}
		// System.out.println("settrue");
		// active = true;
	}

	private void addInventorys() {
		storageInventorys = new HashSet<BlockPos>();
		imInventorys = new HashSet<BlockPos>();
		exInventorys = new HashSet<BlockPos>();
		for (BlockPos cable : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(cable);
			if (tile.getKind() == Kind.exKabel) {
				for (EnumFacing face : tile.getConnections()) {
					if (worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
							&& worldObj.getChunkFromBlockCoords(
									cable.offset(face)).isLoaded())
						exInventorys.add(cable.offset(face));
				}
			} else if (tile.getKind() == Kind.imKabel) {
				for (EnumFacing face : tile.getConnections()) {
					if (worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
							&& worldObj.getChunkFromBlockCoords(
									cable.offset(face)).isLoaded())
						imInventorys.add(cable.offset(face));
				}
			} else if (tile.getKind() == Kind.storageKabel) {
				for (EnumFacing face : tile.getConnections()) {
					if (worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
							&& worldObj.getChunkFromBlockCoords(
									cable.offset(face)).isLoaded())
						storageInventorys.add(cable.offset(face));
				}
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
		if (worldObj.getTotalWorldTime() % 20 != 0)
			return;
		if (cables == null)
			refreshNetwork();
		for (BlockPos p : cables) {
			if (worldObj.getTileEntity(p) != null
					&& ((TileKabel) worldObj.getTileEntity(p)).getKind() == Kind.vacuumKabel) {
				int range = 6;

				int x = getPos().getX();
				int y = getPos().getY();
				int z = getPos().getZ();

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
						int rest = insertStack(stack);
						ItemStack r = stack.copy();
						r.stackSize = rest;
						if (rest <= 0)
							item.setDead();
						else
							item.setEntityItemStack(stack);
					}
				}
			}
		}
	}

	public int insertStack(ItemStack stack) {
		List<IInventory> invs = new ArrayList<IInventory>();
		for (BlockPos p : storageInventorys) {
			IInventory inv = (IInventory) worldObj.getTileEntity(p);
			if (inv == null)
				continue;
			invs.add(inv);
		}
		return Inv.addToInventoriesWithLeftover(stack, invs, false);
	}

	public void impor() {
		if (worldObj.getTotalWorldTime() % 20 != 0)
			return;
		if (imInventorys == null || storageInventorys == null)
			refreshNetwork();
		List<IInventory> imInvs = new ArrayList<IInventory>();
		for (BlockPos p : imInventorys) {
			IInventory inv = (IInventory) worldObj.getTileEntity(p);
			if (inv == null)
				continue;
			imInvs.add(inv);
		}
		List<IInventory> storageInvs = new ArrayList<IInventory>();
		for (BlockPos p : storageInventorys) {
			IInventory inv = (IInventory) worldObj.getTileEntity(p);
			if (inv == null)
				continue;
			storageInvs.add(inv);
		}
		// System.out.println("im: " + imInvs);
		// System.out.println("stor; " + storageInvs);
		for (IInventory inv : imInvs) {
			if (!(inv instanceof ISidedInventory)) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s == null)
						continue;
					int num = inv.getStackInSlot(i).stackSize;
					int rest = Inv.addToInventoriesWithLeftover(s.copy(),
							storageInvs, false);
					if (num == rest)
						continue;
					inv.setInventorySlotContents(
							i,
							rest > 0 ? Inv.copyStack(inv.getStackInSlot(i)
									.copy(), rest) : null);
					inv.markDirty();
					break;

				}
			}
		}
	}

	@Override
	public void update() {
		vacuum();
		impor();
	}

	public Set<BlockPos> getCables() {
		return cables;
	}

}
