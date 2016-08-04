package mrriegel.storagenetwork.gui.request;

import java.util.List;

import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.StacksMessage;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.collect.Lists;

public class ContainerRequest extends Container {
	public InventoryPlayer playerInv;
	public TileRequest tile;
	public InventoryCraftResult result;
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);

	public ContainerRequest(final TileRequest tile, final InventoryPlayer playerInv) {
		this.tile = tile;
		this.playerInv = playerInv;
		result = new InventoryCraftResult();
		for (int i = 0; i < 9; i++) {
			craftMatrix.setInventorySlotContents(i, tile.matrix.get(i));
		}

		SlotCrafting x = new SlotCrafting(playerInv.player, craftMatrix, result, 0, 101, 128) {
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
						ItemStack req = t.request(lis.get(i) != null ? new FilterItem(lis.get(i), true, false, false) : null, 1, false);
						// if (req == null)
						// req = t.request(lis.get(i) != null ? new
						// FilterItem(lis.get(i), false, false, false) : null,
						// 1, false);
						// if (req == null)
						// req = t.request(lis.get(i) != null ? new
						// FilterItem(lis.get(i), false, true, false) : null, 1,
						// false);
						craftMatrix.setInventorySlotContents(i, req);
					}
				List<StackWrapper> list = t.getStacks();
				PacketHandler.INSTANCE.sendTo(new StacksMessage(list, t.getCraftableStacks(list)), (EntityPlayerMP) playerIn);
				detectAndSendChanges();
			}
		};

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
		this.result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, tile.getWorld()));
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		slotChanged();
		super.onContainerClosed(playerIn);
	}

	public void slotChanged() {
		for (int i = 0; i < 9; i++) {
			tile.matrix.put(i, craftMatrix.getStackInSlot(i));
		}
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

			if (slotIndex == 0) {
				craftShift(playerIn, (TileMaster) this.tile.getWorld().getTileEntity(this.tile.getMaster()));
				return null;
			}
			if (slotIndex <= 9) {
				if (!this.mergeItemStack(itemstack1, 10, 10 + 36, true)) {
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
					List<StackWrapper> list = tile.getStacks();
					PacketHandler.INSTANCE.sendTo(new StacksMessage(list, tile.getCraftableStacks(list)), (EntityPlayerMP) playerIn);
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
				return null;
			}
			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	public void craftShift(EntityPlayer player, TileMaster tile) {
		SlotCrafting sl = new SlotCrafting(player, craftMatrix, result, 0, 0, 0);
		int crafted = 0;
		List<ItemStack> lis = Lists.newArrayList();
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++)
			lis.add(craftMatrix.getStackInSlot(i));
		ItemStack res = result.getStackInSlot(0);
		while (crafted + res.stackSize <= res.getMaxStackSize()) {
			if (ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(playerInv), res.copy(), true) != null)
				break;
			ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(playerInv), res.copy(), false);
			sl.onPickupFromSlot(player, res);
			crafted += res.stackSize;
			for (int i = 0; i < craftMatrix.getSizeInventory(); i++)
				if (craftMatrix.getStackInSlot(i) == null) {
					ItemStack req = tile.request(lis.get(i) != null ? new FilterItem(lis.get(i), true, false, false) : null, 1, false);
					// if (req == null)
					// req = t.request(lis.get(i) != null ? new
					// FilterItem(lis.get(i), false, false, false) : null,
					// 1, false);
					// if (req == null)
					// req = t.request(lis.get(i) != null ? new
					// FilterItem(lis.get(i), false, true, false) : null, 1,
					// false);
					craftMatrix.setInventorySlotContents(i, req);
				}
			onCraftMatrixChanged(craftMatrix);
			if (!ItemHandlerHelper.canItemStacksStack(res, result.getStackInSlot(0)))
				break;
			else
				res = result.getStackInSlot(0);
		}

		List<StackWrapper> list = tile.getStacks();
		PacketHandler.INSTANCE.sendTo(new StacksMessage(list, tile.getCraftableStacks(list)), (EntityPlayerMP) player);
		detectAndSendChanges();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (tile == null || tile.getMaster() == null || !(tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster))
			return false;
		TileMaster t = (TileMaster) tile.getWorld().getTileEntity(tile.getMaster());
		if (!tile.getWorld().isRemote && tile.getWorld().getTotalWorldTime() % 40 == 0) {
			List<StackWrapper> list = t.getStacks();
			PacketHandler.INSTANCE.sendTo(new StacksMessage(list, t.getCraftableStacks(list)), (EntityPlayerMP) playerIn);
		}
		return playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.result && super.canMergeSlot(stack, slot);
	}

}
