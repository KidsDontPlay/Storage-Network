package mrriegel.storagenetwork.gui.remote;

import java.util.ArrayList;
import java.util.List;

import mrriegel.storagenetwork.gui.request.InventoryRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.StacksMessage;
import mrriegel.storagenetwork.network.SyncMessage;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.util.Constants;

public class ContainerRemote extends Container {
	public InventoryPlayer playerInv;
	String inv = "";

	public ContainerRemote(final InventoryPlayer playerInv) {
		this.playerInv = playerInv;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9,
						8 + j * 18, 174 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
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
				int rest = tile.insertStack(itemstack1, null);
				ItemStack stack = rest == 0 ? null : Inv.copyStack(itemstack1,
						rest);
				slot.putStack(stack);
				detectAndSendChanges();
				PacketHandler.INSTANCE.sendTo(
						new StacksMessage(tile.getStacks(), GuiHandler.REMOTE),
						(EntityPlayerMP) playerIn);
				if (stack == null)
					return null;
				slot.onPickupFromSlot(playerIn, itemstack1);
				return itemstack1;
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!playerIn.worldObj.isRemote
				&& playerIn.worldObj.getTotalWorldTime() % 50 == 0)
			PacketHandler.INSTANCE.sendTo(
					new StacksMessage(ItemRemote
							.getTile(playerIn.getHeldItem()).getStacks(),
							GuiHandler.REMOTE), (EntityPlayerMP) playerIn);
		return playerIn.getHeldItem() != null
				&& playerIn.getHeldItem().getItem() == ModItems.remote;
	}

}