package mrriegel.storagenetwork.tile;

import net.minecraft.util.BlockPos;

public interface IConnection {
	public BlockPos getMaster();

	public void setMaster(BlockPos master);
}
