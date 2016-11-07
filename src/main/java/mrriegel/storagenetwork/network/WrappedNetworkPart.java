package mrriegel.storagenetwork.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import mrriegel.storagenetwork.tile.INetworkPart;

public abstract class WrappedNetworkPart<T> implements INetworkPart {

	public World world;
    public BlockPos pos;
	public T object;
	
	public WrappedNetworkPart(World world, BlockPos pos, T object) {
		super();
		this.world = world;
		this.pos = pos;
		this.object = object;
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(obj.equals(object))
			return true;
		if (getClass() != obj.getClass())
			return false;
		WrappedNetworkPart<?> other = (WrappedNetworkPart<?>) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

}
