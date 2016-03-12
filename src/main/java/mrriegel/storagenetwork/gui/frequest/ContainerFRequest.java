package mrriegel.storagenetwork.gui.frequest;

import mrriegel.storagenetwork.gui.CrunchItemInventory;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.network.FluidsMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.tile.TileFRequest;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ContainerFRequest extends Container {
	public InventoryPlayer playerInv;
	public TileFRequest tile;
	public IInventory inv;

	public ContainerFRequest(final TileFRequest tile, final InventoryPlayer playerInv) {
		this.tile = tile;
		this.playerInv = playerInv;
		inv = new CrunchItemInventory(2, 1, new ItemStack(Items.fire_charge));
		// NBTTagCompound nbt = new NBTTagCompound();
		// tile.writeToNBT(nbt);
		// inv.setInventorySlotContents(0,
		// ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("fill")));
		// inv.setInventorySlotContents(1,
		// ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("drain")));
		inv.setInventorySlotContents(0, tile.fill);
		inv.setInventorySlotContents(1, tile.drain);
		this.addSlotToContainer(new Slot(inv, 0, 8, 138));
		this.addSlotToContainer(new Slot(inv, 1, 44, 138));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
		}

	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		slotChanged();
		super.onContainerClosed(playerIn);

	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
		ItemStack k = super.slotClick(slotId, clickedButton, mode, playerIn);
		slotChanged();
		return k;
	}

	public void slotChanged() {
		tile.fill = inv.getStackInSlot(0);
		tile.drain = inv.getStackInSlot(1);
		tile.getWorld().markBlockForUpdate(tile.getPos());
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
		return null;
		// ItemStack itemstack = null;
		// Slot slot = this.inventorySlots.get(slotIndex);
		// if (slot != null && slot.getHasStack()) {
		// ItemStack itemstack1 = slot.getStack();
		// itemstack = itemstack1.copy();
		//
		// if (slot.getSlotIndex() == x.getSlotIndex())
		// if (x.crafted + itemstack.stackSize > itemstack.getMaxStackSize()) {
		// x.crafted = 0;
		// return null;
		// }
		// if (slotIndex <= 15) {
		// if (!this.mergeItemStack(itemstack1, 15, 15 + 37, true)) {
		// x.crafted = 0;
		// return null;
		// }
		// slot.onSlotChange(itemstack1, itemstack);
		// } else {
		// if (!this.mergeItemStack(itemstack1, 10, 16, false)) {
		// x.crafted = 0;
		// return null;
		// }
		// }
		// if (itemstack1.stackSize == 0) {
		// slot.putStack((ItemStack) null);
		// } else {
		// slot.onSlotChanged();
		// }
		//
		// if (itemstack1.stackSize == itemstack.stackSize) {
		// x.crafted = 0;
		// return null;
		// }
		// slot.onPickupFromSlot(playerIn, itemstack1);
		// if (slot.getSlotIndex() == x.getSlotIndex()) {
		// x.crafted += itemstack.stackSize;
		// }
		// } else
		// x.crafted = 0;
		//
		// return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!playerIn.worldObj.isRemote && playerIn.worldObj.getTotalWorldTime() % 20 == 0) {
			ItemStack drain = tile.drain;
			TileMaster mas = (TileMaster) playerIn.worldObj.getTileEntity(tile.getMaster());
			if (drain != null && FluidContainerRegistry.isFilledContainer(drain)) {
				int rest = mas.insertFluid(FluidContainerRegistry.getFluidForFilledItem(drain), null);
				if (rest > 0) {
					mas.frequest(FluidContainerRegistry.getFluidForFilledItem(drain).getFluid(), FluidContainerRegistry.getFluidForFilledItem(drain).amount - rest, false);
				} else {
					ItemStack drained = FluidContainerRegistry.drainFluidContainer(drain);
					if (drained != null) {
						inv.setInventorySlotContents(1, drained);
						slotChanged();
					}
				}
			} else if (drain != null && drain.getItem() instanceof IFluidContainerItem) {
				IFluidContainerItem flui = (IFluidContainerItem) drain.getItem();
				if (flui.getFluid(drain) != null) {
					int rest = mas.insertFluid(flui.getFluid(drain), null);
					FluidStack drained = flui.drain(drain, flui.getFluid(drain).amount - rest, true);
					inv.setInventorySlotContents(1, drain);
					slotChanged();
				}
			}
			PacketHandler.INSTANCE.sendTo(new FluidsMessage(mas.getFluids(), GuiHandler.FREQUEST), (EntityPlayerMP) playerIn);
		}
		if (tile == null || tile.getMaster() == null || !(tile.getWorld().getTileEntity(tile.getMaster()) instanceof TileMaster))
			return false;
		return true;
	}
}