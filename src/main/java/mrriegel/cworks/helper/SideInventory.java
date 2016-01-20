package mrriegel.cworks.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public class SideInventory implements IInventory{
ISidedInventory inv;
EnumFacing face;
public SideInventory(ISidedInventory inv, EnumFacing face) {
	super();
	this.inv = inv;
	this.face = face;
}
@Override
public String getName() {
	return inv.getName();
}
@Override
public boolean hasCustomName() {
	return inv.hasCustomName();
}
@Override
public IChatComponent getDisplayName() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public int getSizeInventory() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public ItemStack getStackInSlot(int index) {
	// TODO Auto-generated method stub
	return null;
}
@Override
public ItemStack decrStackSize(int index, int count) {
	// TODO Auto-generated method stub
	return null;
}
@Override
public ItemStack removeStackFromSlot(int index) {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void setInventorySlotContents(int index, ItemStack stack) {
	// TODO Auto-generated method stub
	
}
@Override
public int getInventoryStackLimit() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void markDirty() {
	// TODO Auto-generated method stub
	
}
@Override
public boolean isUseableByPlayer(EntityPlayer player) {
	// TODO Auto-generated method stub
	return false;
}
@Override
public void openInventory(EntityPlayer player) {
	// TODO Auto-generated method stub
	
}
@Override
public void closeInventory(EntityPlayer player) {
	// TODO Auto-generated method stub
	
}
@Override
public boolean isItemValidForSlot(int index, ItemStack stack) {
	// TODO Auto-generated method stub
	return false;
}
@Override
public int getField(int id) {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void setField(int id, int value) {
	// TODO Auto-generated method stub
	
}
@Override
public int getFieldCount() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void clear() {
	// TODO Auto-generated method stub
	
}

}
