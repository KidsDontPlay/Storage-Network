package mrriegel.storagenetwork.gui.container;

import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileContainer;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerContainer extends Container {
	InventoryPlayer playerInv;
	public TileContainer tile;

	public ContainerContainer(TileContainer tile, InventoryPlayer playerInv) {
		this.playerInv = playerInv;
		this.tile = tile;
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(tile, i, 8 + i * 18, 26) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack != null && stack.isItemEqual(new ItemStack(ModItems.template)) && stack.getTagCompound() != null && stack.getTagCompound().getTag("res") != null;
				}

				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					if (getStack() != null)
						getStack().getTagCompound().setLong("machine", ((TileEntity) inventory).getPos().toLong());
				}
			});
		}
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

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (!itemstack.isItemEqual(new ItemStack(ModItems.template)) || itemstack.getTagCompound().getTag("res") == null)
				return null;
			if (slotIndex <= 8) {
				if (!this.mergeItemStack(itemstack1, 8, 8 + 37, true))
					return null;
				slot.onSlotChange(itemstack1, itemstack);
			} else {
				if (!this.mergeItemStack(itemstack1, 0, 9, false))
					return null;
			}
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}
}
