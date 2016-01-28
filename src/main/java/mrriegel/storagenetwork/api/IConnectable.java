package mrriegel.storagenetwork.api;

import net.minecraft.util.BlockPos;

public interface IConnectable {
	public BlockPos getMaster();

	public void setMaster(BlockPos master);
}
