package mrriegel.storagenetwork.gui.frequest;

import java.util.ArrayList;
import java.util.List;

import mrriegel.storagenetwork.gui.CrunchItemInventory;
import mrriegel.storagenetwork.gui.request.SlotCrafting;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.StacksMessage;
import mrriegel.storagenetwork.network.SyncMessage;
import mrriegel.storagenetwork.tile.TileFRequest;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ContainerFRequest extends Container {
	public InventoryPlayer playerInv;
	public TileFRequest tile;
	public IInventory inv;

	public ContainerFRequest(final TileFRequest tile, final InventoryPlayer playerInv) {
		this.tile = tile;
		this.playerInv = playerInv;
		inv = new CrunchItemInventory(2, new ItemStack(Items.fire_charge));
		 NBTTagCompound nbt = new NBTTagCompound();
		 tile.writeToNBT(nbt);
		 inv.setInventorySlotContents(0,
		 ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("fill")));
		 inv.setInventorySlotContents(1,
		 ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("drain")));
		// inv.setInventorySlotContents(0, tile.fill);
		// inv.setInventorySlotContents(1, tile.drain);
		this.addSlotToContainer(new Slot(inv, 0, 20, 100));
		this.addSlotToContainer(new Slot(inv, 1, 40, 110));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
		}

	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		slotChanged();
		super.onContainerClosed(playerIn);

	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
		slotChanged();
		return super.slotClick(slotId, clickedButton, mode, playerIn);
	}

	public void slotChanged() {
		tile.fill=inv.getStackInSlot(0);
		tile.drain=inv.getStackInSlot(1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
		return null;
		// ItemStack itemstack = null;
		// Slot slot = this.inventorySlots.get(slotIndex);
		// if (slot != null && slot.getHasStack()) {
		// ItemStack itemstack1 = slot.getStack();
		// itemstack = itemstack1.copy();
		//
		// if (slot.getSlotIndex() == x.getSlotIndex())
		// if (x.crafted + itemstack.stackSize > itemstack.getMaxStackSize()) {
		// x.crafted = 0;
		// return null;
		// }
		// if (slotIndex <= 15) {
		// if (!this.mergeItemStack(itemstack1, 15, 15 + 37, true)) {
		// x.crafted = 0;
		// return null;
		// }
		// slot.onSlotChange(itemstack1, itemstack);
		// } else {
		// if (!this.mergeItemStack(itemstack1, 10, 16, false)) {
		// x.crafted = 0;
		// return null;
		// }
		// }
		// if (itemstack1.stackSize == 0) {
		// slot.putStack((ItemStack) null);
		// } else {
		// slot.onSlotChanged();
		// }
		//
		// if (itemstack1.stackSize == itemstack.stackSize) {
		// x.crafted = 0;
		// return null;
		// }
		// slot.onPickupFromSlot(playerIn, itemstack1);
		// if (slot.getSlotIndex() == x.getSlotIndex()) {
		// x.crafted += itemstack.stackSize;
		// }
		// } else
		// x.crafted = 0;
		//
		// return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (tile == null || tile.getMaster() == null || !(tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster))
			return false;
		return true;
	}

}