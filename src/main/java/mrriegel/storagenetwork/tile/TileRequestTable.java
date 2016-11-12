package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class TileRequestTable extends TileNetworkPart {

	public List<ItemStack> matrix = new ArrayList<ItemStack>(9);
	public Sort sort = Sort.NAME;
	public boolean topDown = true;

	@Override
	public List<ItemStack> getDroppingItems() {
		return matrix;
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		if (getNetworkCore() != null) {
			if (worldObj.getBlockState(pos).getBlock() == Registry.blockRequestTable)
				player.openGui(StorageNetwork.instance, GuiID.REQUEST_TABLE.ordinal(), worldObj, getX(), getY(), getZ());
			else if (worldObj.getBlockState(pos).getBlock() == Registry.blockRequestCraftingTable)
				;
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		matrix = NBTHelper.getItemStackList(compound.getCompoundTag("matrix"), "matrix");
		sort = Sort.values()[compound.getInteger("sort")];
		topDown = compound.getBoolean("top");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound ss = new NBTTagCompound();
		NBTHelper.setItemStackList(ss, "matrix", matrix);
		compound.setTag("matrix", ss);
		compound.setInteger("sort", sort.ordinal());
		compound.setBoolean("top", topDown);
		return super.writeToNBT(compound);
	}

	public boolean isCraftingTable() {
		return worldObj.getBlockState(pos).getBlock() == Registry.blockRequestCraftingTable;
	}

}
