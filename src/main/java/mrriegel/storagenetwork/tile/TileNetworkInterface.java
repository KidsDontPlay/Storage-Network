package mrriegel.storagenetwork.tile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import com.google.common.collect.Lists;

public class TileNetworkInterface extends TileNetworkPart {

	IItemHandler itemhandler = EmptyHandler.INSTANCE;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (getNetworkCore() == null || getNetworkCore().network == null)
				return (T) EmptyHandler.INSTANCE;
			return (T) itemhandler;
		}
		return super.getCapability(capability, facing);
	}

	public void refreshItemhandler() {
		if (getNetworkCore() == null || getNetworkCore().network == null)
			itemhandler = EmptyHandler.INSTANCE;
		else {
			List<INetworkStorage<IItemHandler, ItemStack>> lis = Lists.newArrayList();
			for (INetworkPart part : getNetworkCore().network.noCables) {
				if (part instanceof INetworkStorage && ((INetworkStorage<?, ?>) part).getStorage() instanceof IItemHandler) {
					lis.add((INetworkStorage<IItemHandler, ItemStack>) part);
				}
			}
			itemhandler = new INetworkStorage.ItemHandlerWrapper(getNetworkCore());
		}
		for (EnumFacing f : EnumFacing.VALUES)
			worldObj.notifyBlockOfStateChange(pos.offset(f), worldObj.getBlockState(pos.offset(f)).getBlock());
	}

}
