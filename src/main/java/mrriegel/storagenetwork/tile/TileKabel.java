package mrriegel.storagenetwork.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileKabel extends TileEntity implements IConnectable {
	private Kind kind;
	private BlockPos master, connectedInventory;
	private EnumFacing inventoryFace;
	private Map<Integer, ItemStack> filter = new HashMap<Integer, ItemStack>();
	private boolean meta = true, white;
	private int priority;
	private Deque<Integer> deque = new ArrayDeque<Integer>();
	private boolean mode = true;
	private int limit = 0;
	ItemStack stack = null;

	public enum Kind {
		kabel, exKabel, imKabel, storageKabel, vacuumKabel;
	}

	public TileKabel() {
	}

	public TileKabel(World w, Block b) {
		kind = getKind(w, b);
	}

	public int elements(int num) {
		int res = 0;
		Deque<Integer> d = new ArrayDeque<Integer>(deque);
		while (!d.isEmpty()) {
			if (d.pollFirst() == num)
				res++;
		}
		return res;
	}

	public static Kind getKind(World world, Block b) {
		if (b == ModBlocks.kabel)
			return Kind.kabel;
		if (b == ModBlocks.exKabel)
			return Kind.exKabel;
		if (b == ModBlocks.imKabel)
			return Kind.imKabel;
		if (b == ModBlocks.storageKabel)
			return Kind.storageKabel;
		if (b == ModBlocks.vacuumKabel)
			return Kind.vacuumKabel;
		return null;
	}

	public static boolean canTransfer(TileKabel tile, ItemStack stack) {
		List<ItemStack> lis = new ArrayList<ItemStack>();
		for (int i = 0; i < 9; i++) {
			ItemStack s = tile.getFilter().get(i);
			if (s != null)
				lis.add(s.copy());
		}
		if (tile.isWhite()) {
			boolean tmp = false;
			for (ItemStack s : lis) {
				if (tile.isMeta() ? stack.isItemEqual(s) : stack.getItem() == s
						.getItem()) {
					tmp = true;
					break;
				}
			}
			return tmp;
		} else {
			boolean tmp = true;
			for (ItemStack s : lis) {
				if (tile.isMeta() ? stack.isItemEqual(s) : stack.getItem() == s
						.getItem()) {
					tmp = false;
					break;
				}
			}
			return tmp;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		kind = Kind.valueOf(compound.getString("kind"));
		master = new Gson().fromJson(compound.getString("master"),
				new TypeToken<BlockPos>() {
				}.getType());
		connectedInventory = new Gson().fromJson(
				compound.getString("connectedInventory"),
				new TypeToken<BlockPos>() {
				}.getType());
		inventoryFace = EnumFacing.byName(compound.getString("inventoryFace"));
		meta = compound.getBoolean("meta");
		white = compound.getBoolean("white");
		priority = compound.getInteger("prio");
		deque = new Gson().fromJson(compound.getString("deque"),
				new TypeToken<Deque<Integer>>() {
				}.getType());
		mode = compound.getBoolean("mode");
		limit = compound.getInteger("limit");
		if (compound.hasKey("stack", 10))
			stack = (ItemStack.loadItemStackFromNBT(compound
					.getCompoundTag("stack")));
		else
			stack = null;
		// id = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("id"));
		NBTTagList invList = compound.getTagList("crunchTE",
				Constants.NBT.TAG_COMPOUND);
		filter = new HashMap<Integer, ItemStack>();
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			filter.put(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (kind == null)
			kind = getKind(worldObj, worldObj.getBlockState(pos).getBlock());
		compound.setString("kind", kind.toString());
		compound.setString("master", new Gson().toJson(master));
		compound.setString("connectedInventory",
				new Gson().toJson(connectedInventory));
		if (inventoryFace != null)
			compound.setString("inventoryFace", inventoryFace.toString());
		compound.setBoolean("meta", meta);
		compound.setBoolean("white", white);
		compound.setInteger("prio", priority);
		compound.setString("deque", new Gson().toJson(deque));
		compound.setBoolean("mode", mode);
		compound.setInteger("limit", limit);
		if (stack != null)
			compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));

		// NBTTagCompound nbt = new NBTTagCompound();
		// if (id != null) {
		// id.writeToNBT(nbt);
		// }
		// compound.setTag("id", nbt);
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (filter.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				filter.get(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		compound.setTag("crunchTE", invList);

	}

	@Override
	public BlockPos getMaster() {
		return master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
	}

	public Kind getKind() {
		if (kind == null)
			kind = getKind(worldObj, worldObj.getBlockState(pos).getBlock());
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public BlockPos getConnectedInventory() {
		return connectedInventory;
	}

	public void setConnectedInventory(BlockPos connectedInventory) {
		this.connectedInventory = connectedInventory;
	}

	public EnumFacing getInventoryFace() {
		return inventoryFace;
	}

	public void setInventoryFace(EnumFacing inventoryFace) {
		this.inventoryFace = inventoryFace;
	}

	public Map<Integer, ItemStack> getFilter() {
		return filter;
	}

	public void setFilter(Map<Integer, ItemStack> filter) {
		this.filter = filter;
	}

	public boolean isMeta() {
		return meta;
	}

	public void setMeta(boolean meta) {
		this.meta = meta;
	}

	public boolean isWhite() {
		return white;
	}

	public void setWhite(boolean white) {
		this.white = white;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Deque<Integer> getDeque() {
		return deque;
	}

	public void setDeque(Deque<Integer> deque) {
		this.deque = deque;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(getPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}
