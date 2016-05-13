package mrriegel.storagenetwork.blocks;

import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class BlockCover extends Block {

	public BlockCover() {
		super(Material.rock);
		this.setHardness(1.0F);
		this.setCreativeTab(StorageNetwork.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":cover");
	}
	
	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.cover"));
		}

	}

}
