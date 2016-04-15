package mrriegel.storagenetwork.gui.remote;

import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.StacksMessage;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRemote extends Container {
	public InventoryPlayer playerInv;

	public ContainerRemote(final InventoryPlayer playerInv) {
		this.playerInv = playerInv;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			if (i == playerInv.currentItem)
				this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						return false;
					}
				});
			else
				this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
		if (playerIn.worldObj.isRemote)
			return null;
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			TileMaster tile = ItemRemote.getTile(playerIn.getHeldItem());
			if (tile != null) {
				int rest = tile.insertStack(itemstack1, null, false);
				ItemStack stack = rest == 0 ? null : Inv.copyStack(itemstack1, rest);
				slot.putStack(stack);
				detectAndSendChanges();
				PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REMOTE), (EntityPlayerMP) playerIn);
				if (stack == null)
					return null;
				slot.onPickupFromSlot(playerIn, itemstack1);
				return null;
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		TileMaster tile = ItemRemote.getTile(playerIn.getHeldItem());
		if (tile == null || !(tile instanceof TileMaster))
			return false;
		if (!playerIn.worldObj.isRemote && playerIn.worldObj.getTotalWorldTime() % 50 == 0)
			PacketHandler.INSTANCE.sendTo(new StacksMessage(ItemRemote.getTile(playerIn.getHeldItem()).getStacks(), tile.getCraftableStacks(), GuiHandler.REMOTE), (EntityPlayerMP) playerIn);
		return playerIn.getHeldItem() != null && playerIn.getHeldItem().getItem() == ModItems.remote;
	}

}