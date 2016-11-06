package mrriegel.storagenetwork.network;

import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.tile.INetworkPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

/**
 * @author canitzp
 */
public class InventoryNetworkPart implements INetworkPart {

    public World world;
    public BlockPos pos;
    public IItemHandler inventory;

    public InventoryNetworkPart(World world, BlockPos pos, IItemHandler inventory){
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
        return this.inventory.getSlots();
    }

}
