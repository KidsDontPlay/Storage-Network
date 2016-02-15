package mrriegel.storagenetwork.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.blocks.PropertyConnection.Connect;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.items.ItemUpgrade;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileKabel extends TileEntity implements IConnectable {
	private Kind kind;
	private BlockPos master, connectedInventory;
	private EnumFacing inventoryFace;
	private Map<Integer, StackWrapper> filter = new HashMap<Integer, StackWrapper>();
	private boolean meta = true, white;
	private int priority;
	private ArrayDeque<Integer> deque = new ArrayDeque<Integer>();
	private boolean mode = true;
	private int limit = 0;
	public Connect north, south, east, west, up, down;
	private Block cover;
	private int coverMeta;

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
		ArrayDeque<Integer> d = new ArrayDeque<Integer>(deque);
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

	public boolean canTransfer(ItemStack stack) {
		List<ItemStack> lis = new ArrayList<ItemStack>();
		for (int i = 0; i < 9; i++) {
			if (getFilter().get(i) == null)
				continue;
			ItemStack s = getFilter().get(i).getStack();
			if (s != null)
				lis.add(s.copy());
		}
		if (isWhite()) {
			boolean tmp = false;
			for (ItemStack s : lis) {
				if (isMeta() ? stack.isItemEqual(s) : stack.getItem() == s.getItem()) {
					tmp = true;
					break;
				}
			}
			return tmp;
		} else {
			boolean tmp = true;
			for (ItemStack s : lis) {
				if (isMeta() ? stack.isItemEqual(s) : stack.getItem() == s.getItem()) {
					tmp = false;
					break;
				}
			}
			return tmp;
		}
	}

	public boolean status() {
		if (elements(ItemUpgrade.OP) < 1)
			return true;
		TileMaster m = (TileMaster) worldObj.getTileEntity(getMaster());
		if (getStack() == null)
			return true;
		int amount = m.getAmount(getStack());
		if (isMode()) {
			return amount > getLimit();
		} else {
			return amount <= getLimit();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		kind = Kind.valueOf(compound.getString("kind"));
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		connectedInventory = new Gson().fromJson(compound.getString("connectedInventory"), new TypeToken<BlockPos>() {
		}.getType());
		inventoryFace = EnumFacing.byName(compound.getString("inventoryFace"));
		meta = compound.getBoolean("meta");
		white = compound.getBoolean("white");
		priority = compound.getInteger("prio");
		coverMeta = compound.getInteger("coverMeta");
		deque = new Gson().fromJson(compound.getString("deque"), new TypeToken<ArrayDeque<Integer>>() {
		}.getType());
		if (deque == null)
			deque = new ArrayDeque<Integer>();
		mode = compound.getBoolean("mode");
		limit = compound.getInteger("limit");
		if (compound.hasKey("stack", 10))
			stack = (ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack")));
		else
			stack = null;
		NBTTagList invList = compound.getTagList("crunchTE", Constants.NBT.TAG_COMPOUND);
		filter = new HashMap<Integer, StackWrapper>();
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			filter.put(slot, StackWrapper.loadStackWrapperFromNBT(stackTag));
		}
		try {
			north = Connect.valueOf(compound.getString("north"));
			south = Connect.valueOf(compound.getString("south"));
			east = Connect.valueOf(compound.getString("east"));
			west = Connect.valueOf(compound.getString("west"));
			up = Connect.valueOf(compound.getString("up"));
			down = Connect.valueOf(compound.getString("down"));
		} catch (Exception e) {
		}
		String fs = compound.getString("cover");
		if (fs == null || "null".equals(fs)) {
			cover = null;
		} else {
			cover = Block.getBlockFromName(fs);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (kind == null)
			kind = getKind(worldObj, worldObj.getBlockState(pos).getBlock());
		compound.setString("kind", kind.toString());
		compound.setString("master", new Gson().toJson(master));
		compound.setString("connectedInventory", new Gson().toJson(connectedInventory));
		if (inventoryFace != null)
			compound.setString("inventoryFace", inventoryFace.toString());
		compound.setBoolean("meta", meta);
		compound.setBoolean("white", white);
		compound.setInteger("prio", priority);
		compound.setInteger("coverMeta", coverMeta);
		compound.setString("deque", new Gson().toJson(deque));
		compound.setBoolean("mode", mode);
		compound.setInteger("limit", limit);
		if (stack != null)
			compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));

		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (filter.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				filter.get(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		try {
			compound.setTag("crunchTE", invList);
			compound.setString("north", north.toString());
			compound.setString("south", south.toString());
			compound.setString("east", east.toString());
			compound.setString("west", west.toString());
			compound.setString("up", up.toString());
			compound.setString("down", down.toString());
		} catch (Exception e) {
		}
		if (cover != null) {
			compound.setString("cover", Block.blockRegistry.getNameForObject(cover).toString());
		} else {
			compound.setString("cover", "null");
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		double renderExtention = 1.0d;
		AxisAlignedBB bb = AxisAlignedBB.fromBounds(pos.getX() - renderExtention, pos.getY() - renderExtention, pos.getZ() - renderExtention, pos.getX() + 1 + renderExtention, pos.getY() + 1 + renderExtention, pos.getZ() + 1 + renderExtention);
		return bb;
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

	public Map<Integer, StackWrapper> getFilter() {
		return filter;
	}

	public void setFilter(Map<Integer, StackWrapper> filter) {
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

	public ArrayDeque<Integer> getDeque() {
		return deque;
	}

	public void setDeque(ArrayDeque<Integer> deque) {
		this.deque = deque;
	}

	public boolean isMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public Block getCover() {
		return cover;
	}

	public void setCover(Block cover) {
		this.cover = cover;
	}

	public int getCoverMeta() {
		return coverMeta;
	}

	public void setCoverMeta(int coverMeta) {
		this.coverMeta = coverMeta;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}
