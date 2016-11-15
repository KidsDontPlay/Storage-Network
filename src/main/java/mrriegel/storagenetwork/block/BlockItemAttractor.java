package mrriegel.storagenetwork.block;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileItemAttractor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockItemAttractor extends CommonBlockContainer<TileItemAttractor> {

	public BlockItemAttractor() {
		super(Material.IRON, "block_item_attractor");
		setHardness(2.5F);
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileItemAttractor();
	}

	@Override
	protected Class<? extends TileItemAttractor> getTile() {
		return TileItemAttractor.class;
	}

}
