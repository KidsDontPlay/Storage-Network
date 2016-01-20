package mrriegel.cworks.tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mrriegel.cworks.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileKabel extends TileEntity {
	private Set<EnumFacing> connections;
	private Kind kind;
	private BlockPos master;
	private int priority;
	private Map<Integer, ItemStack> filter = new HashMap<Integer, ItemStack>();
	private boolean meta, stock;

	public enum Kind {
		kabel, exKabel, imKabel, storageKabel, vacuumKabel;
	}

	public TileKabel() {
	}

	public TileKabel(World w, Block b) {
		connections = new HashSet<EnumFacing>();
		kind = getKind(w, b);
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

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		connections = new Gson().fromJson(compound.getString("connections"),
				new TypeToken<Set<EnumFacing>>() {
				}.getType());
		kind = Kind.valueOf(compound.getString("kind"));
		master = new Gson().fromJson(compound.getString("master"),
				new TypeToken<BlockPos>() {
				}.getType());
		priority = compound.getInteger("priority");
		meta = compound.getBoolean("meta");
		stock = compound.getBoolean("stock");
		NBTTagList invList = compound.getTagList("crunchTE",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < 9; i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			filter.put(i, ItemStack.loadItemStackFromNBT(stackTag));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("connections", new Gson().toJson(connections));
		if (kind == null)
			kind = getKind(worldObj, worldObj.getBlockState(pos).getBlock());
		compound.setString("kind", kind.toString());
		compound.setString("master", new Gson().toJson(master));
		compound.setInteger("priority", priority);
		compound.setBoolean("meta", meta);
		compound.setBoolean("stock", stock);
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

	public Set<EnumFacing> getConnections() {
		return connections;
	}

	public void setConnections(Set<EnumFacing> connections) {
		this.connections = connections;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public BlockPos getMaster() {
		return master;
	}

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
}
