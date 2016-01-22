package mrriegel.cworks.gui;

import mrriegel.cworks.network.PacketHandler;
import mrriegel.cworks.network.StacksMessage;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileMaster;
import mrriegel.cworks.tile.TileRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerRequest extends Container {
	InventoryPlayer playerInv;
	TileRequest tile;

	public ContainerRequest(TileRequest tile, InventoryPlayer playerInv) {
		this.tile = tile;
		this.playerInv = playerInv;
		if (!tile.getWorld().isRemote)
			PacketHandler.INSTANCE.sendTo(new StacksMessage(((TileMaster) tile
					.getWorld().getTileEntity(tile.getMaster())).getStacks()),
					(EntityPlayerMP) playerInv.player);
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
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (tile.getWorld().getTotalWorldTime() % 60 == 0)
			PacketHandler.INSTANCE.sendTo(new StacksMessage(((TileMaster) tile
					.getWorld().getTileEntity(tile.getMaster())).getStacks()),
					(EntityPlayerMP) playerInv.player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
