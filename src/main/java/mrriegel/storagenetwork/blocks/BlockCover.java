package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCover extends Block {

	public BlockCover() {
		super(Material.rock);
		this.setHardness(1.0F);
		this.setCreativeTab(StorageNetwork.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":cover");
	}

}
