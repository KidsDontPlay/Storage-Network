package mrriegel.storagenetwork.gui.request;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;

public class SlotCrafting extends net.minecraft.inventory.SlotCrafting {
	public int crafted = 0;

	public SlotCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory p_i45790_3_, int slotIndex, int xPosition, int yPosition) {
		super(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition);
	}

}
