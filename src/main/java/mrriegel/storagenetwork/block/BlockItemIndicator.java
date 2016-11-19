package mrriegel.storagenetwork.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.storagenetwork.CreativeTab;

public class BlockItemIndicator extends CommonBlockContainer<CommonTile>{

	public BlockItemIndicator(Material materialIn, String name) {
		super(Material.IRON, "block_item_indicator");
		setHardness(2.5F);
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends CommonTile> getTile() {
		// TODO Auto-generated method stub
		return null;
	}

}
