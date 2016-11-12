package mrriegel.storagenetwork.tile;

import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.item.ItemStack;

public interface INetworkStorage<T>{

	T getStorage();
	
	GlobalBlockPos getStoragePosition();
	
	boolean canInsert();
	
	boolean canExtract();
	
	boolean canTransferItem(ItemStack stack);
}
