package mrriegel.storagenetwork.api;

import java.awt.image.TileObserver;

import net.minecraft.util.BlockPos;

public interface IConnectable {
	public BlockPos getMaster();

	public void setMaster(BlockPos master);
}
