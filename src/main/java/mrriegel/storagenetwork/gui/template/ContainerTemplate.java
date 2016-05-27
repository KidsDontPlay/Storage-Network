package mrriegel.storagenetwork.gui.template;

import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.lwjgl.input.Keyboard;

public class ContainerTemplate extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	public ItemStack storedInv;
	public World worldObj;
	public InventoryPlayer playerInv;

	public ContainerTemplate(InventoryPlayer playerInventory) {
		this.worldObj = playerInventory.player.worldObj;
		this.storedInv = playerInventory.player.inventory.getCurrentItem();
		this.playerInv = playerInventory;
		if (storedInv != null && storedInv.getTagCompound() != null) {
			NBTTagList invList = storedInv.getTagCompound().getTagList("crunchItem", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < invList.tagCount(); i++) {
				NBTTagCompound stackTag = invList.getCompoundTagAt(i);
				int slot = stackTag.getByte("Slot");
				if (slot >= 0 && slot < craftMatrix.getSizeInventory()) {
					craftMatrix.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
				}
			}
			NBTTagCompound res = (NBTTagCompound) storedInv.getTagCompound().getTag("res");
			if (res != null)
				craftResult.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(res));
		}
		for (int i = 1; i < 10; i++) {
			if (!NBTHelper.hasTag(storedInv.getTagCompound(), "meta" + i))
				NBTHelper.setBoolean(storedInv, "meta" + i, true);
		}
		this.addSlotToContainer(new Slot(this.craftResult, 0, 124, 35));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
		}
	}

	public void slotChanged(boolean refresh) {
		if (storedInv.getItemDamage() == 0 && refresh)
			this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
		if (!storedInv.hasTagCompound())
			storedInv.setTagCompound(new NBTTagCompound());
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			if (craftMatrix.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				craftMatrix.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		storedInv.getTagCompound().setTag("crunchItem", invList);
		NBTTagCompound res = new NBTTagCompound();
		if (craftResult.getStackInSlot(0) != null) {
			craftResult.getStackInSlot(0).writeToNBT(res);
		}
		storedInv.getTagCompound().setTag("res", res);
		playerInv.mainInventory[playerInv.currentItem] = storedInv;
	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer playerIn) {
		if (slotId >= 10 || slotId < 0)
			return super.slotClick(slotId, clickedButton, mode, playerIn);
		if (playerInv.getItemStack() == null) {
			if (slotId != 0)
				getSlot(slotId).putStack(null);
			else {
				if (getSlot(slotId).getHasStack()) {
					if (clickedButton == 0)
						getSlot(slotId).getStack().stackSize += (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 10 : 1);
					else if (clickedButton == 1)
						getSlot(slotId).getStack().stackSize -= (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 10 : 1);
					else if (clickedButton == 2)
						getSlot(slotId).putStack(null);
					if (getSlot(slotId).getStack() != null && getSlot(slotId).getStack().stackSize <= 0)
						getSlot(slotId).putStack(null);

				}
			}
			slotChanged(slotId != 0);
		} else {
			ItemStack s = playerInv.getItemStack().copy();
			s.stackSize = 1;
			getSlot(slotId).putStack(s);
			slotChanged(slotId != 0);
		}
		return playerInv.getItemStack();

	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() == ModItems.template;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}

}
