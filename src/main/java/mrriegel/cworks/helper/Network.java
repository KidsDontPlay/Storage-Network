package mrriegel.cworks.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class Network {
	public TileEntity master;
	public World world;
	public BlockPos masterPos;
	public List<IInventory> inventories;

	public Network(TileEntity master, List<IInventory> inventories) {
		this.master = master;
		this.inventories = inventories;
		world = ((TileEntity) master).getWorld();
		masterPos = ((TileEntity) master).getPos();
	}

	public boolean addIInventory(IInventory inv) {
		boolean in = false;
		for (IInventory i : inventories) {
			if (Inv.isInventorySame(i, inv)) {
				in = true;
				break;
			}
		}
		if (in)
			return false;
		return inventories.add(inv);
	}

	public boolean removeIInventory(IInventory inv) {
		Iterator<IInventory> it = inventories.iterator();
		while (it.hasNext()) {
			IInventory i = it.next();
			if (Inv.isInventorySame(i, inv)) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	public void update() {
		Iterator<IInventory> it = inventories.iterator();
		while (it.hasNext()) {
			IInventory tmp = it.next();
			if (tmp == null)
				it.remove();
			TileEntity t = (TileEntity) tmp;
			if (t.getWorld().getTileEntity(t.getPos()) == null)
				it.remove();
		}
	}

	public List<ItemStack> extract(ItemStack stack, int size) {
		update();
		List<ItemStack> lis = new ArrayList<ItemStack>();
		for (IInventory inv : inventories) {
			// for()
		}
		return lis;
	}

	public void insert() {
		update();
//		if (master == null)
//			return;
//		for (int i = 0; i < master.getSizeInventory(); i++) {
//			if (master.getStackInSlot(i) == null)
//				continue;
//			master.setInventorySlotContents(i, Inv.copyStack(master
//					.getStackInSlot(i), Inv.addToInventoriesWithLeftover(
//					master.getStackInSlot(i), inventories, false)));
//			if (master.getStackInSlot(i).stackSize == 0)
//				master.setInventorySlotContents(i, null);
//			master.markDirty();
//			world.markBlockForUpdate(masterPos);
//			break;
//		}
	}
}
