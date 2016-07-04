package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.storagenetwork.api.IConnectable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileInterface extends TileEntity implements IConnectable {

	private BlockPos master;

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		return compound;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public BlockPos getMaster() {
		return master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

	private net.minecraftforge.items.IItemHandler itemHandler;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) (itemHandler == null ? (itemHandler = createItemHandler()) : itemHandler);
		return super.getCapability(capability, facing);
	}

	private IItemHandler createItemHandler() {
		if (master != null && worldObj.getTileEntity(master) instanceof TileMaster) {
			TileMaster m = (TileMaster) worldObj.getTileEntity(master);
			List<IItemHandler> lis = Lists.newArrayList();
			for (BlockPos p : m.connectables) {
				if (worldObj.getTileEntity(p) instanceof AbstractFilterTile) {
					AbstractFilterTile x = (AbstractFilterTile) worldObj.getTileEntity(p);
					if (x.isStorage() && x.getInventory() != null)
						lis.add(x.getInventory());
				}
			}
			return new CombinedItemHandler((IItemHandler[]) lis.toArray());
		} else
			return null;
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
		return (master != null && worldObj.getTileEntity(master) instanceof TileMaster && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
	}

	public class CombinedItemHandler implements IItemHandler {

		protected final IItemHandler[] itemHandler;
		protected final int[] baseIndex;
		protected final int slotCount;

		public CombinedItemHandler(IItemHandler... itemHandler) {
			this.itemHandler = itemHandler;
			this.baseIndex = new int[itemHandler.length];
			int index = 0;
			for (int i = 0; i < itemHandler.length; i++) {
				index += itemHandler[i].getSlots();
				baseIndex[i] = index;
			}
			this.slotCount = index;
		}

		protected int getIndexForSlot(int slot) {
			if (slot < 0)
				return -1;

			for (int i = 0; i < baseIndex.length; i++) {
				if (slot - baseIndex[i] < 0) {
					return i;
				}
			}
			return -1;
		}

		protected IItemHandler getHandlerFromIndex(int index) {
			if (index < 0 || index >= itemHandler.length) {
				return EmptyHandler.INSTANCE;
			}
			return itemHandler[index];
		}

		protected int getSlotFromIndex(int slot, int index) {
			if (index <= 0 || index >= baseIndex.length) {
				return slot;
			}
			return slot - baseIndex[index - 1];
		}

		@Override
		public int getSlots() {
			return slotCount;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			int index = getIndexForSlot(slot);
			IItemHandler handler = getHandlerFromIndex(index);
			slot = getSlotFromIndex(slot, index);
			return handler.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			int index = getIndexForSlot(slot);
			IItemHandler handler = getHandlerFromIndex(index);
			slot = getSlotFromIndex(slot, index);
			return handler.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			int index = getIndexForSlot(slot);
			IItemHandler handler = getHandlerFromIndex(index);
			slot = getSlotFromIndex(slot, index);
			return handler.extractItem(slot, amount, simulate);
		}
	}
}
