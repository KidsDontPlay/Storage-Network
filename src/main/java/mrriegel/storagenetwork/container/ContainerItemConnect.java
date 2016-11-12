package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.tile.TileNetworkExporter;
import mrriegel.storagenetwork.tile.TileNetworkImporter;
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
		super(invPlayer, Pair.of("filter", new InventoryBasic("filter", false, 1)), Pair.of("upgrades", new InventoryBasic("upgrades", false, 4)));
		this.tile = tile;
		invs.get("filter").setInventorySlotContents(0, tile.filter);
		for (int i = 0; i < 4; i++)
			invs.get("upgrades").setInventorySlotContents(i, tile.upgrades.get(i));

		addSlotToContainer(new Slot(invs.get("filter"), 0, 8, 9) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Registry.itemFilter;
			}

			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				tile.filter = getStack();
			}
		});
		if (tile instanceof TileNetworkExporter || tile instanceof TileNetworkImporter)
			for (int i = 0; i < 4; i++)
				addSlotToContainer(new Slot(invs.get("upgrades"), i, 30 + 18 * i, 9) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						tile.upgrades.set(getSlotIndex(), getStack());
					}

					@Override
					public boolean isItemValid(ItemStack stack) {
						return stack.getItem() == Registry.itemUpgrade;
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
		else if (inv instanceof InventoryPlayer && stack.getItem() == Registry.itemUpgrade)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("upgrades")));
		else
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
	}

}
