package mrriegel.storagenetwork.network;

import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

/**
 * @author canitzp
 */
public class InventoryNetworkPart extends WrappedNetworkPart<IItemHandler> {

	public InventoryNetworkPart(World world, BlockPos pos, IItemHandler object) {
		super(world, pos, object);
	}

    @Override
    public GlobalBlockPos getPosition() {
        return new GlobalBlockPos(this.pos, this.world.provider.getDimension());
    }

    @Override
    public int getInventorySpace() {
        return this.object.getSlots();
    }

}
