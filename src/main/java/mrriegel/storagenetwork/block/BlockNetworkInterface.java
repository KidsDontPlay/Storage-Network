package mrriegel.storagenetwork.block;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileNetworkInterface;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNetworkInterface extends CommonBlockContainer<TileNetworkInterface> {

	public BlockNetworkInterface() {
		super(Material.IRON, "block_network_interface");
		setHardness(2.5F);
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkInterface();
	}

	@Override
	protected Class<? extends TileNetworkInterface> getTile() {
		return TileNetworkInterface.class;
	}

}
