package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.FilterItem;
import mrriegel.storagenetwork.message.MessageItemListRequest;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerRequestTable extends CommonContainer {

	public TileRequestTable tile;

	public ContainerRequestTable(InventoryPlayer invPlayer, TileRequestTable tile) {
		super(invPlayer, Pair.of("matrix", new InventoryBasic("matrix", false, 9)), Pair.of("result", new InventoryCraftResult()));
		this.tile = tile;
		invs.put("matrix", new InventoryCrafting(this, 3, 3));
		for (int i = 0; i < tile.matrix.size(); i++)
			getMatrix().setInventorySlotContents(i, tile.matrix.get(i));
		addSlotToContainer(new SlotCrafting(invPlayer.player, getMatrix(), invs.get("result"), 0, 44, 196) {
			@Override
			public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
				if (playerIn.worldObj.isRemote) {
					return;
				}
				List<ItemStack> lis = Lists.newArrayList();
				for (int i = 0; i < getMatrix().getSizeInventory(); i++)
					if (getMatrix().getStackInSlot(i) == null)
						lis.add(null);
					else
						lis.add(getMatrix().getStackInSlot(i).copy());
				super.onPickupFromSlot(playerIn, stack);
				TileNetworkCore core = tile.getNetworkCore();
				detectAndSendChanges();
				for (int i = 0; i < getMatrix().getSizeInventory(); i++)
					if (getMatrix().getStackInSlot(i) == null && lis.get(i) != null) {
						ItemStack req = core.network.requestItem(new FilterItem(lis.get(i), true, false, true), 1, false);
						getMatrix().setInventorySlotContents(i, req);
					}
				PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) playerIn);
				detectAndSendChanges();
			}
		});
		initSlots("matrix", 8, 138, 3, 3);
		initPlayerSlots(80, 138);
	}

	@Override
	protected void initSlots() {
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		if (inv == invs.get("matrix"))
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
		return null;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);
		if (!playerIn.worldObj.isRemote && slot.getHasStack()) {
			TileNetworkCore core = tile.getNetworkCore();
			IInventory inv = slot.inventory;
			if (inv instanceof InventoryPlayer) {
				slot.putStack(core.network.insertItem(slot.getStack(), null, false));
				detectAndSendChanges();
				PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) playerIn);
				return null;
			} else if (inv == invs.get("result")) {
				craftShift(core);
				return null;
			}
		}
		return super.transferStackInSlot(playerIn, index);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		tile.matrix.clear();
		for (int i = 0; i < 9; i++)
			tile.matrix.add(getMatrix().getStackInSlot(i));
	}

	public void craftShift(TileNetworkCore core) {
		IInventory result = invs.get("result");
		SlotCrafting sl = new SlotCrafting(invPlayer.player, getMatrix(), result, 0, 0, 0);
		//		SlotCrafting sl = (SlotCrafting) inventorySlots.get(0);
		int crafted = 0;
		List<ItemStack> lis = Lists.newArrayList();
		for (int i = 0; i < getMatrix().getSizeInventory(); i++)
			lis.add(getMatrix().getStackInSlot(i));
		ItemStack res = result.getStackInSlot(0);
		detectAndSendChanges();
		while (crafted + res.stackSize <= res.getMaxStackSize()) {
			if (ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(invPlayer), res.copy(), true) != null)
				break;
			ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(invPlayer), res.copy(), false);
			sl.onPickupFromSlot(invPlayer.player, res);
			crafted += res.stackSize;
			for (int i = 0; i < getMatrix().getSizeInventory(); i++)
				if (getMatrix().getStackInSlot(i) == null && lis.get(i) != null) {
					ItemStack req = core.network.requestItem(new FilterItem(lis.get(i), true, false, true), 1, false);
					getMatrix().setInventorySlotContents(i, req);
				}
			onCraftMatrixChanged(null);
			if (!ItemHandlerHelper.canItemStacksStack(res, result.getStackInSlot(0)))
				break;
			else
				res = result.getStackInSlot(0);
		}
		PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) invPlayer.player);
		detectAndSendChanges();
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		invs.get("result").setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(getMatrix(), tile.getWorld()));
		tile.matrix.clear();
		for (int i = 0; i < 9; i++)
			tile.matrix.add(getMatrix().getStackInSlot(i));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!playerIn.worldObj.isRemote && playerIn.worldObj.getTotalWorldTime() % 100 == 0)
			PacketHandler.sendTo(new MessageItemListRequest(tile.getNetworkCore().network.getItemstacks()), (EntityPlayerMP) invPlayer.player);
		return super.canInteractWith(playerIn);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return slot.inventory != invs.get("result") && super.canMergeSlot(stack, slot);
	}

	public InventoryCrafting getMatrix() {
		return (InventoryCrafting) invs.get("matrix");
	}

}
