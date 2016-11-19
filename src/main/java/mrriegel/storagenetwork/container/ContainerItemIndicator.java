package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.gui.slot.SlotGhost;
import mrriegel.storagenetwork.tile.TileItemIndicator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerItemIndicator extends CommonContainer {

	public TileItemIndicator tile;

	public ContainerItemIndicator(InventoryPlayer invPlayer, TileItemIndicator tile) {
		super(invPlayer, Pair.of("filter", new InventoryBasic("filter", false, 1) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}
		}));
		this.tile = tile;
		invs.get("filter").setInventorySlotContents(0, tile.stack);
		addSlotToContainer(new SlotGhost(invs.get("filter"), 0, 8, 9) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				tile.stack = getStack();
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
		if (inv instanceof InventoryPlayer)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("filter")));
		else
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
	}

}
