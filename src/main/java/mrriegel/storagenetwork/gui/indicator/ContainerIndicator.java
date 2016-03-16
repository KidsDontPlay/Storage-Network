package mrriegel.storagenetwork.gui.indicator;

import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.tile.TileIndicator;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerIndicator extends Container {
	InventoryPlayer playerInv;
	public TileIndicator tile;
	private StackWrapper filter;

	public ContainerIndicator(TileIndicator tile, InventoryPlayer playerInv) {
		this.playerInv = playerInv;
		this.tile = tile;
		filter = tile.getStack();
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 - 39 + 10 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142 - 39 + 10));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return tile != null && tile.getMaster() != null && tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster;
	}

	public void slotChanged() {
		tile.setStack(filter);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		Slot slot = this.inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			if (itemstack1 == null)
				return null;
			if (filter == null) {
				filter = new StackWrapper(itemstack1.copy(), itemstack1.stackSize);
				slotChanged();
			}
		}
		return null;
	}

	public StackWrapper getFilter() {
		return filter;
	}

	public void setFilter(StackWrapper filter) {
		this.filter = filter;
	}
}
