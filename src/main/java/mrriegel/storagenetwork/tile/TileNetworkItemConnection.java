package mrriegel.storagenetwork.tile;

import java.util.List;

import com.google.common.collect.Lists;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileNetworkItemConnection extends TileNetworkConnection implements IPriority{

	public ItemStack filter;
	private int priority;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("filter"))
			filter = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("filter"));
		priority=compound.getInteger("priority");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (filter != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			filter.writeToNBT(nbt);
			compound.setTag("filter", nbt);
		}
		compound.setInteger("priority", priority);
		return super.writeToNBT(compound);
	}
	
	public int getPriority() {
		return priority;
	}
	
	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(StorageNetwork.instance, GuiID.ITEM_CONNECTOR.ordinal(), worldObj, getX(), getY(), getZ());
		return true;
	}
	
	@Override
	public List<ItemStack> getDroppingItems() {
		return Lists.newArrayList(filter);
	}
}
