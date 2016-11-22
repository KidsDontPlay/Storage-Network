package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.gui.slot.SlotGhost;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.tile.TileNetworkStock;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class ContainerStock extends CommonContainer {

	public TileNetworkStock tile;

	public ContainerStock(InventoryPlayer invPlayer, TileNetworkStock tile) {
		super(invPlayer, Pair.of("items", new InventoryBasic("items", false, 8) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}
		}), Pair.of("upgrades", new InventoryBasic("upgrades", false, 4) {
			@Override
			public int getInventoryStackLimit() {
				return 1;
			}
		}));
		this.tile = tile;
		for (int i = 0; i < 4; i++)
			invs.get("upgrades").setInventorySlotContents(i, tile.upgrades.get(i));
		for (int i = 0; i < 8; i++)
			invs.get("items").setInventorySlotContents(i, tile.items.get(i));
		for (int i = 0; i < 4; i++)
			addSlotToContainer(new Slot(invs.get("upgrades"), i, 30 + 18 * i, 9) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() == Registry.upgrade;
				}

				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					tile.upgrades.set(getSlotIndex(), getStack());
					tile.markDirty();
				}
			});
		for (int i = 0; i < 4; i++)
			addSlotToContainer(new SlotGhost(invs.get("items"), i, 8, 30 + 18 * i) {
				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					tile.items.set(getSlotIndex(), getStack());
					tile.markDirty();
				}
			});
		for (int i = 0; i < 4; i++)
			addSlotToContainer(new SlotGhost(invs.get("items"), i + 4, 87, 30 + 18 * i) {
				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					tile.items.set(getSlotIndex(), getStack());
					tile.markDirty();
				}
			});
		initPlayerSlots(8, 104);
	}

	@Override
	protected void initSlots() {
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		if (inv instanceof InventoryPlayer && stack.getItem() == Registry.upgrade)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("upgrades")));
		else if (inv instanceof InventoryPlayer)
			return Lists.newArrayList(getAreaForEntireInv(invs.get("items")));
		else
			return Lists.newArrayList(getAreaForEntireInv(invPlayer));
	}

}
