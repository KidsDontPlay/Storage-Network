package mrriegel.storagenetwork.gui.cable;

import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ContainerCable extends Container {
	InventoryFilter inv;
	InventoryPlayer playerInv;
	TileKabel tile;

	public ContainerCable(TileKabel tile, InventoryPlayer playerInv) {
		this.playerInv = playerInv;
		this.tile = tile;
		inv = new InventoryFilter(tile, 9);
		// id = new InventoryFilter(tile, 1);
		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			this.addSlotToContainer(new Slot(inv, j, 8 + j * 18, 26));
		}
		// this.addSlotToContainer(new Slot(id, 0, 8, -18));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9,
						8 + j * 18, 84 - 39 + 10 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18,
					142 - 39 + 10));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public void slotChanged() {
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		nbt.setTag("crunchTE", invList);
		// if (id.getStackInSlot(0) != null)
		// nbt.setTag("stack", id.getStackInSlot(0).writeToNBT(new
		// NBTTagCompound()));
		tile.readFromNBT(nbt);
	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, int mode,
			EntityPlayer playerIn) {
		if (slotId >= 9 || slotId == -999)
			return super.slotClick(slotId, clickedButton, mode, playerIn);
		if (playerInv.getItemStack() == null) {
			getSlot(slotId).putStack(null);
			slotChanged();
		} else {
			ItemStack s = playerInv.getItemStack().copy();
			if (!in(s)) {
				s.stackSize = 1;
				getSlot(slotId).putStack(s);
				slotChanged();
			}
		}
		return playerInv.getItemStack();

	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();

			if (slotIndex <= 8) {
				slot.putStack(null);
			} else {
				int s = getSlot(itemstack1);
				if (s != -1) {
					ItemStack in = itemstack1.copy();
					in.stackSize = 1;
					getSlot(s).putStack(in);
					getSlot(s).onSlotChanged();
					slotChanged();
				}

			}
		}
		return null;
	}

	int getSlot(ItemStack stack) {
		if (in(stack))
			return -1;
		for (int i = 0; i < 9; i++) {
			if (!getSlot(i).getHasStack())
				return i;
		}
		return -1;
	}

	boolean in(ItemStack stack) {
		for (int i = 0; i < 9; i++) {
			if (getSlot(i).getHasStack()
					&& getSlot(i).getStack().isItemEqual(stack))
				return true;
		}
		return false;
	}

}
