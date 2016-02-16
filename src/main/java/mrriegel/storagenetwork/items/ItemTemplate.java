package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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
