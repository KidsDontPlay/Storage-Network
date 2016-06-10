package mrriegel.storagenetwork.tile;

import java.util.Arrays;
import java.util.List;

import mrriegel.storagenetwork.blocks.BlockKabel.Connect;
import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.items.ItemUpgrade;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileKabel extends AbstractFilterTile {
	private BlockPos connectedInventory;
	private EnumFacing inventoryFace;
	private List<ItemStack> upgrades = Arrays.asList(null, null, null, null);
	private boolean mode = true;
	private int limit = 0;
	public Connect north, south, east, west, up, down;
	private Block cover;
	private int coverMeta;
	private boolean disabled;

	ItemStack stack = null;

	public enum Kind {
		kabel, exKabel, imKabel, storageKabel, vacuumKabel, fexKabel, fimKabel, fstorageKabel;

		public boolean isFluid() {
			return this == Kind.fexKabel || this == Kind.fimKabel || this == Kind.fstorageKabel;
		}
	}

	public int elements(int num) {
		int res = 0;
		for (ItemStack s : upgrades) {
			if (s != null && s.getItemDamage() == num) {
				res += s.stackSize;
				break;
			}
		}
		return res;
	}

	@Override
	public boolean isFluid() {
		return getKind().isFluid();
	}

	public boolean isUpgradeable() {
		Kind kind = getKind();
		return kind == Kind.exKabel || kind == Kind.imKabel || kind == Kind.fexKabel || kind == Kind.fimKabel;
	}

	public static Kind getKind(Block b) {
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
		if (b == ModBlocks.fexKabel)
			return Kind.fexKabel;
		if (b == ModBlocks.fimKabel)
			return Kind.fimKabel;
		if (b == ModBlocks.fstorageKabel)
			return Kind.fstorageKabel;
		return null;
	}

	public boolean status() {
		if (elements(ItemUpgrade.OP) < 1)
			return true;
		if (!isFluid()) {
			TileMaster m = (TileMaster) worldObj.getTileEntity(getMaster());
			if (getStack() == null)
				return true;
			int amount = m.getAmount(new FilterItem(getStack(), true, false, false));
			if (isMode()) {
				return amount > getLimit();
			} else {
				return amount <= getLimit();
			}
		} else {
			TileMaster m = (TileMaster) worldObj.getTileEntity(getMaster());
			if (Util.getFluid(getStack()) == null)
				return true;
			int amount = m.getAmount(Util.getFluid(getStack()).getFluid());
			if (isMode()) {
				return amount > getLimit();
			} else {
				return amount <= getLimit();
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		connectedInventory = new Gson().fromJson(compound.getString("connectedInventory"), new TypeToken<BlockPos>() {
		}.getType());
		inventoryFace = EnumFacing.byName(compound.getString("inventoryFace"));

		coverMeta = compound.getInteger("coverMeta");
		mode = compound.getBoolean("mode");
		limit = compound.getInteger("limit");
		if (compound.hasKey("stack", 10))
			stack = (ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack")));
		else
			stack = null;
		if (compound.hasKey("north"))
			north = Connect.valueOf(compound.getString("north"));
		if (compound.hasKey("south"))
			south = Connect.valueOf(compound.getString("south"));
		if (compound.hasKey("east"))
			east = Connect.valueOf(compound.getString("east"));
		if (compound.hasKey("west"))
			west = Connect.valueOf(compound.getString("west"));
		if (compound.hasKey("up"))
			up = Connect.valueOf(compound.getString("up"));
		if (compound.hasKey("down"))
			down = Connect.valueOf(compound.getString("down"));

		String fs = compound.getString("cover");
		if (fs == null || "null".equals(fs)) {
			cover = null;
		} else {
			cover = Block.getBlockFromName(fs);
		}
		disabled = compound.getBoolean("disabled");

		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		upgrades = Arrays.asList(null, null, null, null);
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			if (j >= 0 && j < 4) {
				upgrades.set(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("connectedInventory", new Gson().toJson(connectedInventory));
		if (inventoryFace != null)
			compound.setString("inventoryFace", inventoryFace.toString());
		compound.setInteger("coverMeta", coverMeta);
		compound.setBoolean("mode", mode);
		compound.setInteger("limit", limit);
		if (stack != null)
			compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));

		if (north != null)
			compound.setString("north", north.toString());
		if (south != null)
			compound.setString("south", south.toString());
		if (east != null)
			compound.setString("east", east.toString());
		if (west != null)
			compound.setString("west", west.toString());
		if (up != null)
			compound.setString("up", up.toString());
		if (down != null)
			compound.setString("down", down.toString());

		if (cover != null) {
			compound.setString("cover", Block.REGISTRY.getNameForObject(cover).toString());
		} else {
			compound.setString("cover", "null");
		}
		compound.setBoolean("disabled", disabled);

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < upgrades.size(); ++i) {
			if (upgrades.get(i) != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				upgrades.get(i).writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		compound.setTag("Items", nbttaglist);
		return compound;

	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		double renderExtention = 1.0d;
		AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - renderExtention, pos.getY() - renderExtention, pos.getZ() - renderExtention, pos.getX() + 1 + renderExtention, pos.getY() + 1 + renderExtention, pos.getZ() + 1 + renderExtention);
		return bb;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean enabled) {
		this.disabled = enabled;
	}

	public Kind getKind() {
		if (worldObj == null)
			return null;
		return getKind(worldObj.getBlockState(pos).getBlock());
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

	public List<ItemStack> getUpgrades() {
		return upgrades;
	}

	public void setUpgrades(List<ItemStack> upgrades) {
		this.upgrades = upgrades;
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
	public IFluidHandler getFluidTank() {
		if (getKind() == Kind.fstorageKabel && getConnectedInventory() != null && worldObj.getTileEntity(getConnectedInventory()) instanceof IFluidHandler)
			return (IFluidHandler) worldObj.getTileEntity(getConnectedInventory());
		return null;
	}

	@Override
	public IInventory getInventory() {
		if (getKind() == Kind.storageKabel && getConnectedInventory() != null && worldObj.getTileEntity(getConnectedInventory()) instanceof IInventory)
			return (IInventory) worldObj.getTileEntity(getConnectedInventory());
		return null;
	}

	@Override
	public BlockPos getSource() {
		return getConnectedInventory();
	}

	@Override
	public boolean isStorage() {
		return getKind() == Kind.storageKabel || getKind() == Kind.fstorageKabel;
	}

}
