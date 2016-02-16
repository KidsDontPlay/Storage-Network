package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemTemplate extends Item {

	public ItemTemplate() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":template");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (stack.getTagCompound() != null) {
			NBTTagCompound res = (NBTTagCompound) stack.getTagCompound().getTag("res");
			ItemStack s = ItemStack.loadItemStackFromNBT(res);
			if (s != null)
				tooltip.add("Output: " + s.getDisplayName());
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if (playerIn.isSneaking()) {
			itemStackIn.setTagCompound(new NBTTagCompound());
		} else {
			playerIn.openGui(StorageNetwork.instance, GuiHandler.TEMPLATE, worldIn, 0, 0, 0);
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}

}
