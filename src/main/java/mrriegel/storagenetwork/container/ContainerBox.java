package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.tile.TileBox;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerBox extends CommonContainer {

	public TileBox<?, ?> tile;

	public ContainerBox(InventoryPlayer invPlayer, TileBox<?, ?> tile) {
		super(invPlayer, Pair.of("filter", new InventoryBasic("filter", false, 1) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}
		}));
		this.tile = tile;
		invs.get("filter").setInventorySlotContents(0, tile.filter);

		addSlotToContainer(new Slot(invs.get("filter"), 0, 8, 9) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Registry.itemFilter;
			}

			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				tile.filter = getStack();
				tile.markDirty();
			}
		});
		initPlayerSlots(8, 32);
	}

	@Override
	protected void initSlots() {
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		if (inv instanceof InventoryPlayer && stack.getItem() == Registry.itemFilter)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("filter")));
		else
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
	}

}
