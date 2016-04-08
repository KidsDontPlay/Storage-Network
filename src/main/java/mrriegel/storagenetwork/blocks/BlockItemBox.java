package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.tile.TileItemBox;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockItemBox extends BlockConnectable {

	public BlockItemBox() {
		super(Material.iron);
		this.setHardness(3.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":itembox");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileItemBox();
	}

}
