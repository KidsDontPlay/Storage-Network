package mrriegel.storagenetwork.gui.fremote;

import mrriegel.storagenetwork.gui.CrunchItemInventory;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemFRemote;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.network.FluidsMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ContainerFRemote extends Container {
	public InventoryPlayer playerInv;
	public IInventory inv;

	public ContainerFRemote(final InventoryPlayer playerInv) {
		this.playerInv = playerInv;
		inv = new CrunchItemInventory(2, 1, new ItemStack(Items.fire_charge));
		this.addSlotToContainer(new Slot(inv, 0, 8, 138));
		this.addSlotToContainer(new Slot(inv, 1, 44, 138));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			if (i == playerInv.currentItem)
				this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						return false;
					}
				});
			else
				this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
		}

	}
	public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!playerIn.worldObj.isRemote)
        {
            for (int i = 0; i < 2; ++i)
            {
                ItemStack itemstack = this.inv.removeStackFromSlot(i);

                if (itemstack != null)
                {
                    playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
		if (playerIn.worldObj.isRemote)
			return null;
		ItemStack itemstack = null;

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		TileMaster mas = ItemFRemote.getTile(playerIn.getHeldItem());
		if (mas == null || !(mas instanceof TileMaster))
			return false;
		if (!playerIn.worldObj.isRemote && playerIn.worldObj.getTotalWorldTime() % 20 == 0) {
			ItemStack drain = inv.getStackInSlot(1);
			if (drain != null && FluidContainerRegistry.isFilledContainer(drain)) {
				int rest = mas.insertFluid(FluidContainerRegistry.getFluidForFilledItem(drain), null);
				if (rest > 0) {
					mas.frequest(FluidContainerRegistry.getFluidForFilledItem(drain).getFluid(), FluidContainerRegistry.getFluidForFilledItem(drain).amount - rest, false);
				} else {
					ItemStack drained = FluidContainerRegistry.drainFluidContainer(drain);
					if (drained != null) {
						inv.setInventorySlotContents(1, drained);
					}
				}
			} else if (drain != null && drain.getItem() instanceof IFluidContainerItem) {
				IFluidContainerItem flui = (IFluidContainerItem) drain.getItem();
				if (flui.getFluid(drain) != null) {
					int rest = mas.insertFluid(flui.getFluid(drain), null);
					FluidStack drained = flui.drain(drain, flui.getFluid(drain).amount - rest, true);
					inv.setInventorySlotContents(1, drain);
				}
			}
			PacketHandler.INSTANCE.sendTo(new FluidsMessage(mas.getFluids(), GuiHandler.FREMOTE), (EntityPlayerMP) playerIn);
		}
		if (!playerIn.worldObj.isRemote && playerIn.worldObj.getTotalWorldTime() % 50 == 0)
			PacketHandler.INSTANCE.sendTo(new FluidsMessage(ItemFRemote.getTile(playerIn.getHeldItem()).getFluids(), GuiHandler.FREMOTE), (EntityPlayerMP) playerIn);
		return playerIn.getHeldItem() != null && playerIn.getHeldItem().getItem() == ModItems.fremote;
	}

}
