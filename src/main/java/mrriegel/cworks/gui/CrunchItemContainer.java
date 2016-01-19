package mrriegel.cworks.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public abstract class CrunchItemContainer extends Container {
	public CrunchItemInventory inv;
	public ItemStack storedInv;
	public InventoryPlayer playerInv;

	public CrunchItemContainer(EntityPlayer player, InventoryPlayer playerInv,
			CrunchItemInventory inv) {
		this.inv = inv;
		storedInv = playerInv.getCurrentItem();
		this.playerInv = playerInv;
		if (storedInv != null && storedInv.getTagCompound() != null) {
			NBTTagList invList = storedInv.getTagCompound().getTagList(
					"crunchItem", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < invList.tagCount(); i++) {
				NBTTagCompound stackTag = invList.getCompoundTagAt(i);
				int slot = stackTag.getByte("Slot");
				if (slot >= 0 && slot < inv.getInv().length) {
					inv.setInventorySlotContents(slot,
							ItemStack.loadItemStackFromNBT(stackTag));
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return inv.isUseableByPlayer(p_75145_1_);
	}

	public void slotChanged() {
		writeInv(storedInv);
		playerInv.mainInventory[playerInv.currentItem] = storedInv;
	}

	private void writeInv(ItemStack con2) {
		if (!con2.hasTagCompound())
			con2.setTagCompound(new NBTTagCompound());
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getInv().length; i++) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		con2.getTagCompound().setTag("crunchItem", invList);

	}

	abstract protected boolean stackAllowed(ItemStack stackInSlot);

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = inventorySlots.get(slot);

		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			if (!stackAllowed(stackInSlot))
				return null;

			stack = stackInSlot.copy();

			if (slot < inv.getSizeInventory()) {
				if (!this.mergeItemStack(stackInSlot, inv.getSizeInventory(),
						36 + inv.getSizeInventory(), true)) {
					return null;
				}
			}

			else if (!this.mergeItemStack(stackInSlot, 0,
					inv.getSizeInventory(), false)) {
				return null;
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

}
