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
import net.minecraft.item.ItemStack;

public class ContainerRequestTable extends CommonContainer {

	public TileRequestTable tile;
	public boolean isCrafting;

	public ContainerRequestTable(InventoryPlayer invPlayer, TileRequestTable tile) {
		super(invPlayer);
		this.tile=tile;
		isCrafting = tile.isCraftingTable();
		if (!tile.getWorld().isRemote)
			PacketHandler.sendTo(new MessageItemListRequest(tile.getNetworkCore().network.getItemstacks()), (EntityPlayerMP) invPlayer.player);
	}

	@Override
	protected void initSlots() {
		// TODO Auto-generated method stub

	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if(!playerIn.worldObj.isRemote&&playerIn.worldObj.getTotalWorldTime()%20==0)
			PacketHandler.sendTo(new MessageItemListRequest(tile.getNetworkCore().network.getItemstacks()), (EntityPlayerMP) invPlayer.player);
		return super.canInteractWith(playerIn);
	}

}
