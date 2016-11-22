package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.item.ItemUpgrade.UpgradeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

public class TileNetworkItemConnection extends TileNetworkConnection implements IPriority {

	public ItemStack filter;
	private int priority;
	public List<ItemStack> upgrades = Lists.newArrayList(null, null, null, null);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("filter"))
			filter = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("filter"));
		priority = compound.getInteger("priority");
		upgrades = NBTHelper.getItemStackList(compound.getCompoundTag("upgrades"), "upgrades");
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
		NBTTagCompound u = new NBTTagCompound();
		NBTHelper.setItemStackList(u, "upgrades", upgrades);
		compound.setTag("upgrades", u);
		return super.writeToNBT(compound);
	}

	@Override
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
		List<ItemStack> lis = Lists.newArrayList(upgrades);
		lis.add(filter);
		return lis;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		switch (nbt.getInteger("buttonID")) {
		case 0:
			priority = Math.max(priority - (nbt.getBoolean("shift") ? 10 : 1), -99);
			break;
		case 1:
			priority = Math.min(priority + (nbt.getBoolean("shift") ? 10 : 1), 99);
			break;
		case 2:
			if (this instanceof TileNetworkStorage)
				((TileNetworkStorage) this).iomode = ((TileNetworkStorage) this).iomode.next();
			break;
		default:
			break;
		}
		markDirty();
	}

	public int getUpgradeAmount(UpgradeType type) {
		int result = 0;
		for (ItemStack stack : upgrades)
			if (stack != null && stack.getItem() == Registry.upgrade && stack.getItemDamage() == type.ordinal())
				result++;
		return result;

	}

	public int getTransferAmount(Class<?> clazz) {
		if (clazz == Item.class || clazz == ItemStack.class) {
			return (int) Math.pow(4, getUpgradeAmount(UpgradeType.STACK));
		} else if (clazz == Fluid.class || clazz == FluidStack.class) {
			return (int) Math.pow(4, getUpgradeAmount(UpgradeType.STACK)) * 100;
		}
		return 0;
	}

	public int getSpeed() {
		return getUpgradeAmount(UpgradeType.SPEED) + 1;
	}

}
