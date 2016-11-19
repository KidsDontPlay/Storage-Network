package mrriegel.storagenetwork.tile;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.FilterItem;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.BlockItemIndicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileItemIndicator extends TileNetworkPart implements ITickable {

	public ItemStack stack;
	public boolean more;
	public int number;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		stack = NBTHelper.getItemStack(compound, "stack");
		more = NBTHelper.getBoolean(compound, "more");
		number = NBTHelper.getInt(compound, "number");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setItemStack(compound, "stack", stack);
		NBTHelper.setBoolean(compound, "more", more);
		NBTHelper.setInt(compound, "number", number);
		return super.writeToNBT(compound);
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(StorageNetwork.instance, GuiID.ITEM_INDICATOR.ordinal(), worldObj, getX(), getY(), getZ());
		return true;
	}

	@Override
	public void update() {
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 30 == 0) {
			boolean old = worldObj.getBlockState(pos).getValue(BlockItemIndicator.STATE);
			boolean neu = false;
			if (getNetworkCore() != null && getNetworkCore().network != null && stack != null) {
				int total = getNetworkCore().network.getAmountOf(new FilterItem(stack));
				if (more) {
					neu = number > total;
				} else {
					neu = number <= total;
				}
			}
			if (old != neu) {
				worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockItemIndicator.STATE, neu), 2);
				markForSync();
				worldObj.notifyNeighborsOfStateChange(pos, worldObj.getBlockState(pos).getBlock());
			}
		}
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		if (nbt.getInteger("buttonID") == 0)
			more = !more;
		if (nbt.getInteger("buttonID") == 1000) {
			try {
				number = Integer.valueOf(nbt.getString("text"));
			} catch (NumberFormatException e) {
				number = 0;
			}
		}
	}

}
