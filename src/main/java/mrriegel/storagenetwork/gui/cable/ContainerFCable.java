package mrriegel.storagenetwork.gui.cable;

import java.util.HashMap;
import java.util.Map;

import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ContainerFCable extends Container {
	InventoryPlayer playerInv;
	public TileKabel tile;
	private Map<Integer, String> filter;

	public ContainerFCable(TileKabel tile, InventoryPlayer playerInv) {
		this.playerInv = playerInv;
		this.tile = tile;
		filter = new HashMap<Integer, String>();
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		NBTTagList invList = nbt.getTagList("crunchTE", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			filter.put(slot, stackTag.getString("Fluid"));
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
		NBTTagList fluidList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (filter.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				stackTag.setString("Fluid", filter.get(i));
				fluidList.appendTag(stackTag);
			}
		}
		nbt.setTag("fluids", fluidList);
		tile.readFromNBT(nbt);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		Slot slot = this.inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			if (itemstack1 == null || getFluid(itemstack1) == null)
				return null;
			for (int i = 0; i < 9; i++) {
				ItemStack f = new ItemStack(getFluid(itemstack1).getBlock());
				System.out.println("block: " + getFluid(itemstack1).getBlock());
				if (filter.get(i) == null && !in(new StackWrapper(f, 1))) {
					System.out.println("putty");
					filter.put(i, new StackWrapper(f.copy(), 1));
					System.out.println(f.getItem());
					// System.out.println(filter.get(i));
					slotChanged();
					break;
				}
			}
		}
		return null;
	}

	boolean in(String s) {
		for (int i = 0; i < 9; i++) {
			if (filter.get(i) != null && filter.get(i).equals(s))
				return true;
		}
		return false;
	}

	public Map<Integer, String> getFilter() {
		return filter;
	}

	public void setFilter(Map<Integer, String> filter) {
		this.filter = filter;
	}

	private Fluid getFluid(ItemStack stack) {
		FluidStack s = FluidContainerRegistry.getFluidForFilledItem(stack);
		System.out.println(stack + " " + s);
		if (s == null)
			return null;
		else
			return s.getFluid();
	}
}
