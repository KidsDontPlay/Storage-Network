package mrriegel.storagenetwork.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mrriegel.storagenetwork.StorageNetwork;

public class ItemSSDD extends Item {
	public ItemSSDD() {
		super();
		this.setCreativeTab(StorageNetwork.tab1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName(StorageNetwork.MODID + ":ssdd");
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 4; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
	tooltip.add("size: "+getSize(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + "_" + stack.getItemDamage();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	public static int getSize(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemSSDD))
			return -1;
		int num = 8;
		for (int i = 0; i < stack.getItemDamage(); i++)
			num *= 8;
		return num;
		// switch (stack.getItemDamage()) {
		// case 0:
		// return 10;
		// case 1:
		// return 100;
		// case 2:
		// return 1000;
		// case 3:
		// return 10000;
		// }
		// return -1;
	}

	public static IInventory getInventory(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemSSDD))
			return new InventoryBasic(null, false, 0);
		IInventory inv = new InventoryBasic(null, false, getSize(stack));
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null)
			compound = new NBTTagCompound();
		NBTTagList nbttaglist = compound.getTagList("Items", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < inv.getSizeInventory()) {
				inv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		return inv;
	}

	public static void setInventory(IInventory inv, ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemSSDD))
			return;
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		stack.getTagCompound().setTag("Items", nbttaglist);

	}
}
