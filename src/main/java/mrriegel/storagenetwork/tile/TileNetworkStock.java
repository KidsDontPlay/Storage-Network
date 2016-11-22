package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class TileNetworkStock extends TileNetworkItemConnection {

	public List<ItemStack> items = Lists.newArrayList(null, null, null, null, null, null, null, null);
	public List<Integer> numbers = Lists.newArrayList(0, 0, 0, 0, 0, 0, 0, 0);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		items = NBTHelper.getItemStackList(compound, "items");
		numbers = NBTHelper.getIntList(compound, "numbers");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setItemStackList(compound, "items", items);
		NBTHelper.setIntList(compound, "numbers", numbers);
		return super.writeToNBT(compound);
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(StorageNetwork.instance, GuiID.STOCK.ordinal(), worldObj, getX(), getY(), getZ());
		return true;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		super.handleMessage(player, nbt);
		if (nbt.getInteger("buttonID") == 1000) {
			try {
				numbers.set(nbt.getInteger("index"), Integer.valueOf(nbt.getString("text")));
			} catch (NumberFormatException e) {
				numbers.set(nbt.getInteger("index"), 0);
			}
		}
	}
}
