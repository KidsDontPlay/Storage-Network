package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTemplate extends Item {

	public ItemTemplate() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName(StorageNetwork.MODID + ":template");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (stack.getTagCompound() != null) {
			NBTTagCompound res = (NBTTagCompound) stack.getTagCompound().getTag("res");
			if (res != null) {
				ItemStack s = ItemStack.loadItemStackFromNBT(res);
				if (s != null)
					tooltip.add("Output: " + s.getDisplayName());
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if (playerIn.isSneaking()) {
			itemStackIn.setTagCompound(new NBTTagCompound());
		} else {
			if (!worldIn.isRemote)
				playerIn.openGui(StorageNetwork.instance, GuiHandler.TEMPLATE, worldIn, 0, 0, 0);
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 2; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + "_" + stack.getItemDamage();
	}

}
