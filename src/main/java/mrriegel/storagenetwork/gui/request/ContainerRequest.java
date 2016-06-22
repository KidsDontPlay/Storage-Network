package mrriegel.storagenetwork.gui.request;

import java.util.List;

import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.StacksMessage;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;

import com.google.common.collect.Lists;

public class ContainerRequest extends Container {
	public InventoryPlayer playerInv;
	public TileRequest tile;
	public InventoryCraftResult result;
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	String inv = "";
	SlotCrafting x;
	long lastTime;

	public ContainerRequest(final TileRequest tile, final InventoryPlayer playerInv) {
		this.tile = tile;
		this.playerInv = playerInv;
		lastTime = System.currentTimeMillis();
		result = new InventoryCraftResult();
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		NBTTagList invList = nbt.getTagList("matrix", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			craftMatrix.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}

		x = new SlotCrafting(playerInv.player, craftMatrix, result, 0, 101, 128) {
			@Override
			public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
				if (playerIn.worldObj.isRemote) {
					return;
				}
				List<ItemStack> lis = Lists.newArrayList();
				for (int i = 0; i < craftMatrix.getSizeInventory(); i++)
					lis.add(craftMatrix.getStackInSlot(i));
				super.onPickupFromSlot(playerIn, stack);
				TileMaster t = (TileMaster) tile.getWorld().getTileEntity(tile.getMaster());
				detectAndSendChanges();
				for (int i = 0; i < craftMatrix.getSizeInventory(); i++)
					if (craftMatrix.getStackInSlot(i) == null) {
						ItemStack req = t.request(lis.get(i) != null ? new FilterItem(lis.get(i), true, true, false) : null, 1, false);
						craftMatrix.setInventorySlotContents(i, req);
					}
				PacketHandler.INSTANCE.sendTo(new StacksMessage(t.getStacks(), t.getCraftableStacks()), (EntityPlayerMP) playerIn);
				detectAndSendChanges();
			}
		};

		if (!tile.getWorld().isRemote) {
			TileMaster t = (TileMaster) tile.getWorld().getTileEntity(tile.getMaster());
			List<FilterItem> lis = Lists.newArrayList();
			// System.out.println("cratfante: "+t.getMissing(null, new
			// FilterItem(new ItemStack(Items.STICK)), 18, true, lis));
			for (FilterItem x : lis)
				System.out.println("     " + x);
		}
		this.addSlotToContainer(x);
		int index = 0;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(craftMatrix, index++, 8 + j * 18, 110 + i * 18));
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
		}
		this.onCraftMatrixChanged(this.craftMatrix);

	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, tile.getWorld()));
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		slotChanged();
		super.onContainerClosed(playerIn);
	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer playerIn) {
		lastTime = System.currentTimeMillis();
		return super.slotClick(slotId, clickedButton, mode, playerIn);
	}

	public void slotChanged() {
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (craftMatrix.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				craftMatrix.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		nbt.setTag("matrix", invList);
		tile.readFromNBT(nbt);
		Util.updateTile(tile.getWorld(), tile.getPos());

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

			if (slot.getSlotIndex() == x.getSlotIndex())
				if (x.crafted + itemstack.stackSize > itemstack.getMaxStackSize()) {
					x.crafted = 0;
					return null;
				}
			if (slotIndex <= 9) {
				if (!this.mergeItemStack(itemstack1, 10, 10 + 36, true)) {
					x.crafted = 0;
					return null;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else {
				TileMaster tile = (TileMaster) this.tile.getWorld().getTileEntity(this.tile.getMaster());
				if (tile != null) {
					int rest = tile.insertStack(itemstack1, null, false);
					ItemStack stack = rest == 0 ? null : ItemHandlerHelper.copyStackWithSize(itemstack1, rest);
					slot.putStack(stack);
					detectAndSendChanges();
					PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks()), (EntityPlayerMP) playerIn);
					if (stack == null)
						return null;
					slot.onPickupFromSlot(playerIn, itemstack1);
					return null;
				}
			}
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				x.crafted = 0;
				return null;
			}
			slot.onPickupFromSlot(playerIn, itemstack1);
			if (slot.getSlotIndex() == x.getSlotIndex()) {
				x.crafted += itemstack.stackSize;
			}
		} else
			x.crafted = 0;

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (tile == null || tile.getMaster() == null || !(tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster))
			return false;
		TileMaster t = (TileMaster) tile.getWorld().getTileEntity(tile.getMaster());
		if (!tile.getWorld().isRemote && tile.getWorld().getTotalWorldTime() % 40 == 0) {
			PacketHandler.INSTANCE.sendTo(new StacksMessage(t.getStacks(), t.getCraftableStacks()), (EntityPlayerMP) playerInv.player);
		}

		if (x.crafted != 0 && Math.abs(System.currentTimeMillis() - lastTime) > 500) {
			x.crafted = 0;
		}
		return playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot p_94530_2_) {
		return p_94530_2_.inventory != this.result && super.canMergeSlot(stack, p_94530_2_);
	}

}
