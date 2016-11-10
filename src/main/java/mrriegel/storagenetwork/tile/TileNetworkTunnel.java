package mrriegel.storagenetwork.tile;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import com.google.common.collect.Lists;

public class TileNetworkTunnel extends TileNetworkConnection {

	public Mode mode;
	public IOMode iomode = IOMode.IN;
	private List<TileNetworkTunnel> tunnels = Lists.newArrayList();

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		mode = Mode.values()[compound.getInteger("mode")];
		iomode = IOMode.values()[compound.getInteger("iomode")];
		List<BlockPos> list = Utils.getBlockPosList(NBTHelper.getLongList(compound, "tunnels"));
		tunnels.clear();
		for (BlockPos pos : list) {
			TileEntity tile = worldObj.getTileEntity(pos);
			if (tile instanceof TileNetworkTunnel)
				tunnels.add((TileNetworkTunnel) tile);
		}
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("mode", mode.ordinal());
		compound.setInteger("iomode", iomode.ordinal());
		List<BlockPos> list = Lists.newArrayList();
		for (TileNetworkTunnel tile : getTunnels())
			list.add(tile.pos);
		NBTHelper.setLongList(compound, "tunnels", Utils.getLongList(list));
		return super.writeToNBT(compound);
	}

	public List<TileNetworkTunnel> getTunnels() {
		tunnels.removeAll(Collections.singleton(null));
		return tunnels;
	}

	public IInventory getInventory() {
		if (getTile() instanceof IInventory)
			return (IInventory) getTile();
		return null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		switch (mode) {
		case ENERGY:
			break;
		case FLUID:
			break;
		case ITEM:
			break;
		default:
			break;
		}
		return super.getCapability(capability, facing);
	}

	public enum Mode {
		ENERGY(Color.orange.getRGB()), ITEM(Color.green.getRGB()), FLUID(Color.blue.getRGB()), REDSTONE(Color.red.getRGB());
		public int color;

		private Mode(int color) {
			this.color = color;
		}
	}

	public enum IOMode {
		IN, OUT, INOUT;
	}
}
