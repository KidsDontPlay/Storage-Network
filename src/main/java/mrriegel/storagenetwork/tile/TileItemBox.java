package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.config.ConfigHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileItemBox extends AbstractFilterTile {

	private InventoryBasic inv = new InventoryBasic(null, false, ConfigHandler.itemBoxCapacity);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readInventory(compound);
	}

	public void readInventory(NBTTagCompound compound) {
		NBTTagList invList = compound.getTagList("box", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSizeInventory()) {
				inv.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeInventory(compound);
		return compound;
	}

	public void writeInventory(NBTTagCompound compound) {
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		compound.setTag("box", invList);
	}

	public InventoryBasic getInv() {
		return inv;
	}

	public void setInv(InventoryBasic inv) {
		this.inv = inv;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public IFluidHandler getFluidTank() {
		return null;
	}

	@Override
	public IInventory getInventory() {
		return getInv();
	}

	@Override
	public BlockPos getSource() {
		return pos;
	}

	@Override
	public boolean isFluid() {
		return false;
	}

	@Override
	public boolean isStorage() {
		return true;
	}

}
