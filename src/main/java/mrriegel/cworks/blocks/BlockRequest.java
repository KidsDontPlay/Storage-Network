package mrriegel.cworks.blocks;

import mrriegel.cworks.CableWorks;
import mrriegel.cworks.CreativeTab;
import mrriegel.cworks.tile.TileRequest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRequest extends BlockContainer{

	public BlockRequest() {
		super(Material.iron);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(CableWorks.MODID + ":request");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRequest();
	}

}
