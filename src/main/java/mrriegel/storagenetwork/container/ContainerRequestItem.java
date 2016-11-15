package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.item.ItemWirelessAccessor;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

public class ContainerRequestItem extends ContainerAbstractRequest<ItemStack> {

	public ContainerRequestItem(InventoryPlayer invPlayer, ItemStack object) {
		super(invPlayer, object);
	}

	@Override
	public List<ItemStack> getMatrixList() {
		return NBTStackHelper.getItemStackList(object, "matrix");
	}

	@Override
	public TileNetworkCore getNetworkCore() {
		return ItemWirelessAccessor.getCore(object);
	}

	@Override
	protected void saveMatrix() {
		List<ItemStack> lis = Lists.newArrayList();
		for (int i = 0; i < 9; i++)
			lis.add(getMatrix().getStackInSlot(i));
		NBTStackHelper.setItemStackList(object, "matrix", lis);
	}

	@Override
	public Sort getSort() {
		return Sort.values()[NBTStackHelper.getInt(object, "sort")];
	}

	@Override
	public boolean isTopdown() {
		return NBTStackHelper.getBoolean(object, "top");
	}

	@Override
	public boolean isJEI() {
		return NBTStackHelper.getBoolean(object, "jei");
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		super.canInteractWith(playerIn);
		return object != null && object.isItemEqual(playerIn.getHeldItemMainhand());
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId >= 0 && getSlot(slotId) != null && getSlot(slotId).getStack() == player.getHeldItemMainhand())
			return null;
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}
