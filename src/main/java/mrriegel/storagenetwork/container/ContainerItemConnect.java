package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.tile.TileNetworkItemConnection;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerItemConnect extends CommonContainer {

	public TileNetworkItemConnection tile;

	public ContainerItemConnect(InventoryPlayer invPlayer, TileNetworkItemConnection tile) {
		super(invPlayer, Pair.of("inv", new InventoryBasic("null", false, 1) ));
		this.tile = tile;
		IInventory inv = invs.get("inv");
		inv.setInventorySlotContents(0, tile.filter);
	}

	@Override
	protected void initSlots() {
		addSlotToContainer(new Slot(invs.get("inv"), 0, 80, 9) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Registry.itemFilter;
			}
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				tile.filter=getStack();
			}
		});
		initPlayerSlots(8, 32);
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		if (inv instanceof InventoryPlayer && stack.getItem() == Registry.itemFilter)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("inv")));
		else
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
	}

}
