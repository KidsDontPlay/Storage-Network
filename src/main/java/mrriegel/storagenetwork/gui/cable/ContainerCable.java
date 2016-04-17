package mrriegel.storagenetwork.gui.cable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCable extends Container {
	InventoryPlayer playerInv;
	public TileKabel tile;
	private Map<Integer, StackWrapper> filter;
	private Map<Integer, Boolean> ores;
	private Map<Integer, Boolean> metas;
	IInventory upgrades;

	public ContainerCable(TileKabel tile, InventoryPlayer playerInv) {
		this.playerInv = playerInv;
		this.tile = tile;
		filter = new HashMap<Integer, StackWrapper>(tile.getFilter());
		ores = new HashMap<Integer, Boolean>(tile.getOres());
		metas = new HashMap<Integer, Boolean>(tile.getMetas());
		upgrades = new InventoryBasic("upgrades", false, 4) {
			@Override
			public int getInventoryStackLimit() {
				return 4;
			}
		};
		if (tile.isUpgradeable()) {
			for (int i = 0; i < tile.getUpgrades().size(); i++) {
				upgrades.setInventorySlotContents(i, tile.getUpgrades().get(i));
			}
			for (int ii = 0; ii < 4; ii++) {
				this.addSlotToContainer(new Slot(upgrades, ii, 98 + ii * 18, 6) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return stack.getItem() == ModItems.upgrade && ((getStack() != null && getStack().getItemDamage() == stack.getItemDamage()) || !in(stack.getItemDamage()));
					}

					@Override
					public void onSlotChanged() {
						slotChanged();
						super.onSlotChanged();
					}

					private boolean in(int meta) {
						for (int i = 0; i < upgrades.getSizeInventory(); i++) {
							if (upgrades.getStackInSlot(i) != null && upgrades.getStackInSlot(i).getItemDamage() == meta)
								return true;
						}
						return false;
					}

				});
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 - 39 + 10 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142 - 39 + 10));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return tile != null && tile.getMaster() != null && tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster;
	}

	public void slotChanged() {
		tile.setFilter(filter);
		tile.setOres(ores);
		tile.setMetas(metas);
		tile.setUpgrades(Arrays.<ItemStack> asList(null, null, null, null));
		for (int i = 0; i < upgrades.getSizeInventory(); i++)
			tile.getUpgrades().set(i, upgrades.getStackInSlot(i));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		Slot slot = this.inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			if (itemstack1 == null)
				return null;
			for (int i = 0; i < 9; i++) {
				if (filter.get(i) == null && !in(new StackWrapper(itemstack1, 1))) {
					filter.put(i, new StackWrapper(itemstack1.copy(), itemstack1.stackSize));
					ores.put(i, false);
					slotChanged();
					break;
				}
			}
		}
		return null;
	}

	boolean in(StackWrapper stack) {
		for (int i = 0; i < 9; i++) {
			if (filter.get(i) != null && filter.get(i).getStack().isItemEqual(stack.getStack()))
				return true;
		}
		return false;
	}

	public Map<Integer, StackWrapper> getFilter() {
		return filter;
	}

	public void setFilter(Map<Integer, StackWrapper> filter) {
		this.filter = filter;
	}

	public Map<Integer, Boolean> getOres() {
		return ores;
	}

	public void setOres(Map<Integer, Boolean> ores) {
		this.ores = ores;
	}

	public Map<Integer, Boolean> getMetas() {
		return metas;
	}

	public void setMetas(Map<Integer, Boolean> metas) {
		this.metas = metas;
	}

}
