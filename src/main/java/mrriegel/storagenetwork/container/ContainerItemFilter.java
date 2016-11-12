package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.gui.slot.SlotGhost;
import mrriegel.limelib.helper.NBTStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerItemFilter extends CommonContainer {

	public ItemStack stack;

	public ContainerItemFilter(InventoryPlayer invPlayer, ItemStack stack) {
		super(invPlayer, Pair.of("inv", new InventoryBasic("null", false, 24)));
		this.stack = stack;
		IInventory inv = invs.get("inv");
		if (!invPlayer.player.worldObj.isRemote)
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				inv.setInventorySlotContents(i, NBTStackHelper.getItemStackList(stack, "inv").get(i));
			}

	}

	@Override
	protected void initSlots() {
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 6; ++j)
				this.addSlotToContainer(new SlotGhost(invs.get("inv"), j + i * 6, 8 + j * 18, 8 + i * 18) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						inventoryChanged();
					}
				});
		initPlayerSlots(8, 84);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		inventoryChanged();
		super.onContainerClosed(playerIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return stack != null && stack.isItemEqual(playerIn.getHeldItemMainhand());
	}

	@Override
	protected void inventoryChanged() {
		if (stack == null)
			return;
		List<ItemStack> list = Lists.newArrayList();
		for (int i = 0; i < invs.get("inv").getSizeInventory(); i++)
			list.add(invs.get("inv").getStackInSlot(i));
		NBTStackHelper.setItemStackList(stack, "inv", list);
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		if (inv instanceof InventoryPlayer)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("inv")));
		return null;
	}

}
