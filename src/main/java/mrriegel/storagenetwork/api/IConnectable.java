package mrriegel.storagenetwork.api;

import net.minecraft.util.math.BlockPos;

public interface IConnectable {
	public BlockPos getMaster();

	public void setMaster(BlockPos master);

}
