package mrriegel.storagenetwork.tile;

import mrriegel.limelib.util.GlobalBlockPos;

public interface INetworkStorage<T, S> {

	T getStorage();

	GlobalBlockPos getStoragePosition();

	boolean canInsert();

	boolean canExtract();

	boolean canTransferItem(S stack);
}
