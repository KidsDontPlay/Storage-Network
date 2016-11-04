package mrriegel.storagenetwork.network;

import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.tile.INetworkPart;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author canitzp
 */
public class InventoryNetworkPart implements INetworkPart {

    public World world;
    public BlockPos pos;
    public IInventory inventory;

    public InventoryNetworkPart(World world, BlockPos pos, IInventory inventory){
        this.world = world;
        this.pos = pos;
        this.inventory = inventory;
    }

    @Override
    public GlobalBlockPos getPosition() {
        return new GlobalBlockPos(this.pos, this.world.provider.getDimension());
    }

    @Override
    public int getInventorySpace() {
        return this.inventory.getSizeInventory();
    }

}
