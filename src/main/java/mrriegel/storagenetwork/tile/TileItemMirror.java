package mrriegel.storagenetwork.tile;

import java.util.List;

import com.google.common.collect.Lists;

import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.StackWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileItemMirror extends TileNetworkPart implements ITickable {

	public List<StackWrapper> wraps = Lists.newArrayList(null, null, null, null);
	public EnumFacing face = EnumFacing.DOWN;
	private long lastEx = System.currentTimeMillis();

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		wraps.clear();
		for (int i = 0; i < 4; i++) {
			wraps.add(StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag(i + "wrap")));
		}
		face = EnumFacing.VALUES[compound.getInteger("face")];
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for (int i = 0; i < 4; i++) {
			if (wraps.get(i) != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				wraps.get(i).writeToNBT(nbt);
				compound.setTag(i + "wrap", nbt);
			}
		}
		compound.setInteger("face", face.ordinal());
		return super.writeToNBT(compound);
	}

	@Override
	public void update() {
		if (!worldObj.isRemote && getNetworkCore() == null) {
			for (StackWrapper w : wraps)
				if (w != null)
					w.setSize(0);
		}
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 50 == 0 && getNetworkCore() != null) {
			markForSync();
		}
	}

	@Override
	public void markForSync() {
		for (StackWrapper w : wraps)
			if (w != null && !worldObj.isRemote && getNetworkCore() != null && getNetworkCore().network != null)
				w.setSize(getNetworkCore().network.getAmountOf(new FilterItem(w.getStack())));
		super.markForSync();
		lastEx = System.currentTimeMillis();
	}

	public boolean canExtract() {
		return System.currentTimeMillis() - lastEx > 130L;
	}

}
