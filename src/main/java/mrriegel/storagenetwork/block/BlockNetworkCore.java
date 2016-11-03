package mrriegel.storagenetwork.block;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.tile.TileEntityNetworkCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author canitzp
 */
public class BlockNetworkCore extends CommonBlockContainer<TileEntityNetworkCore> {

    public BlockNetworkCore() {
        super(Material.IRON, StorageNetwork.MODID + ":block_network_core");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityNetworkCore();
    }

    @Override
    protected Class<? extends TileEntityNetworkCore> getTile() {
        return TileEntityNetworkCore.class;
    }

}
