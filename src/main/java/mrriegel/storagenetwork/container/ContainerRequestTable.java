package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.storagenetwork.message.MessageItemListRequest;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

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
		initSlots("result", 44, 196, 1, 1);
		initSlots("matrix", 8, 138, 3, 3);
		initPlayerSlots(80, 138);
	}

	@Override
	protected void initSlots() {
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		if (inv instanceof InventoryBasic && ((InventoryBasic) inv).getName().equals("matrix"))
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
		return null;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
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

	public InventoryCrafting getMatrix() {
		return (InventoryCrafting) invs.get("matrix");
	}

}
