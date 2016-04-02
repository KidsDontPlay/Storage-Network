package mrriegel.storagenetwork.gui.cable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Lists;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

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
		filter = new HashMap<Integer, StackWrapper>();
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		NBTTagList invList = nbt.getTagList("crunchTE", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			filter.put(slot, StackWrapper.loadStackWrapperFromNBT(stackTag));
		}
		ores = new HashMap<Integer, Boolean>();
		NBTTagList oreList = nbt.getTagList("ores", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < oreList.tagCount(); i++) {
			NBTTagCompound stackTag = oreList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			ores.put(slot, stackTag.getBoolean("Ore"));
		}
		metas = new HashMap<Integer, Boolean>();
		NBTTagList metaList = nbt.getTagList("metas", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < metaList.tagCount(); i++) {
			NBTTagCompound stackTag = metaList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			metas.put(slot, stackTag.getBoolean("Meta"));
		}
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
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (filter.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				filter.get(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		nbt.setTag("crunchTE", invList);
		NBTTagList oreList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (ores.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				stackTag.setBoolean("Ore", ores.get(i));
				oreList.appendTag(stackTag);
			}
		}
		nbt.setTag("ores", oreList);
		NBTTagList metaList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (metas.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				stackTag.setBoolean("Meta", metas.get(i));
				metaList.appendTag(stackTag);
			}
		}
		nbt.setTag("metas", metaList);
		tile.readFromNBT(nbt);
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
