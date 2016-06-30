package mrriegel.storagenetwork.tile;

import java.util.HashMap;
import java.util.Map;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public abstract class AbstractFilterTile extends TileEntity implements IConnectable {

	private Map<Integer, StackWrapper> filter = new HashMap<Integer, StackWrapper>();
	private Map<Integer, Boolean> ores = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> metas = new HashMap<Integer, Boolean>();
	private boolean white;
	private int priority;
	private BlockPos master;
	private Direction way = Direction.BOTH;

	public enum Direction {
		IN, OUT, BOTH;

		public boolean match(Direction way) {
			if (this == BOTH || way == BOTH)
				return true;
			return this == way;
		}

		public Direction next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		readSettings(compound);
	}

	public void readSettings(NBTTagCompound compound) {
		white = compound.getBoolean("white");
		priority = compound.getInteger("prio");
		NBTTagList invList = compound.getTagList("crunchTE", Constants.NBT.TAG_COMPOUND);
		filter = new HashMap<Integer, StackWrapper>();
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			filter.put(slot, StackWrapper.loadStackWrapperFromNBT(stackTag));
		}

		NBTTagList oreList = compound.getTagList("ores", Constants.NBT.TAG_COMPOUND);
		ores = new HashMap<Integer, Boolean>();
		for (int i = 0; i < 9; i++)
			ores.put(i, false);
		for (int i = 0; i < oreList.tagCount(); i++) {
			NBTTagCompound stackTag = oreList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			ores.put(slot, stackTag.getBoolean("Ore"));
		}

		NBTTagList metaList = compound.getTagList("metas", Constants.NBT.TAG_COMPOUND);
		metas = new HashMap<Integer, Boolean>();
		for (int i = 0; i < 9; i++)
			metas.put(i, true);
		for (int i = 0; i < metaList.tagCount(); i++) {
			NBTTagCompound stackTag = metaList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			metas.put(slot, stackTag.getBoolean("Meta"));
		}
		try {
			way = Direction.valueOf(compound.getString("way"));
		} catch (Exception e) {
			way = Direction.BOTH;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		writeSettings(compound);
		return compound;
	}

	public void writeSettings(NBTTagCompound compound) {
		compound.setBoolean("white", white);
		compound.setInteger("prio", priority);
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

		NBTTagList oreList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (ores.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				stackTag.setBoolean("Ore", ores.get(i));
				oreList.appendTag(stackTag);
			}
		}
		compound.setTag("ores", oreList);

		NBTTagList metaList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (metas.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				stackTag.setBoolean("Meta", metas.get(i));
				metaList.appendTag(stackTag);
			}
		}
		compound.setTag("metas", metaList);
		compound.setString("way", way.toString());
	}

	public boolean canTransfer(ItemStack stack, Direction way) {
		if (isStorage() && !this.way.match(way))
			return false;
		if (isWhite()) {
			boolean tmp = false;
			for (int i = 0; i < 9; i++) {
				if (getFilter().get(i) == null)
					continue;
				ItemStack s = getFilter().get(i).getStack();
				if (s == null)
					continue;
				boolean ore = getOre(i);
				boolean meta = getMeta(i);
				if (ore ? Util.equalOreDict(stack, s) : meta ? stack.isItemEqual(s) : stack.getItem() == s.getItem()) {
					tmp = true;
					break;
				}
			}
			return tmp;
		} else {
			boolean tmp = true;
			for (int i = 0; i < 9; i++) {
				if (getFilter().get(i) == null)
					continue;
				ItemStack s = getFilter().get(i).getStack();
				if (s == null)
					continue;
				boolean ore = getOre(i);
				boolean meta = getMeta(i);
				if (ore ? Util.equalOreDict(stack, s) : meta ? stack.isItemEqual(s) : stack.getItem() == s.getItem()) {
					tmp = false;
					break;
				}
			}
			return tmp;
		}
	}

	public boolean canTransfer(Fluid fluid, Direction way) {
		if (isStorage() && !this.way.match(way))
			return false;
		if (isWhite()) {
			boolean tmp = false;
			for (int i = 0; i < 9; i++) {
				if (getFilter().get(i) == null)
					continue;
				ItemStack s = getFilter().get(i).getStack();
				if (s == null)
					continue;
				if (Util.getFluid(s) != null && Util.getFluid(s).getFluid() == fluid) {
					tmp = true;
					break;
				}
			}
			return tmp;
		} else {
			boolean tmp = true;
			for (int i = 0; i < 9; i++) {
				if (getFilter().get(i) == null)
					continue;
				ItemStack s = getFilter().get(i).getStack();
				if (s == null)
					continue;
				if (Util.getFluid(s) != null && Util.getFluid(s).getFluid() == fluid) {
					tmp = false;
					break;
				}
			}
			return tmp;
		}
	}

	@Override
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

	public abstract IFluidHandler getFluidTank();

	public abstract IItemHandler getInventory();

	public abstract BlockPos getSource();

	public abstract boolean isFluid();

	public abstract boolean isStorage();

	public boolean getOre(int i) {
		return getOres().get(i) == null ? false : getOres().get(i);
	}

	public boolean getMeta(int i) {
		return getMetas().get(i) == null ? true : getMetas().get(i);
	}

	public Map<Integer, StackWrapper> getFilter() {
		return filter;
	}

	public void setFilter(Map<Integer, StackWrapper> filter) {
		this.filter = filter;
	}

	public Map<Integer, Boolean> getOres() {
		return ores;
	}

	public void setOres(Map<Integer, Boolean> ores) {
		this.ores = ores;
	}

	public Map<Integer, Boolean> getMetas() {
		return metas;
	}

	public void setMetas(Map<Integer, Boolean> metas) {
		this.metas = metas;
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

	@Override
	public BlockPos getMaster() {
		return master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
	}

	public Direction getWay() {
		return way;
	}

	public void setWay(Direction way) {
		this.way = way;
	}

}
