package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.blocks.BlockIndicator;
import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileIndicator extends TileConnectable implements ITickable {

	private boolean more;
	private StackWrapper stack;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		more = compound.getBoolean("more");
		if (compound.hasKey("stack", 10))
			stack = (StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag("stack")));
		else
			stack = null;

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("more", more);
		if (stack != null)
			compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	public boolean isMore() {
		return more;
	}

	public void setMore(boolean more) {
		this.more = more;
	}

	public StackWrapper getStack() {
		return stack;
	}

	public void setStack(StackWrapper stack) {
		this.stack = stack;
	}

	@Override
	public void update() {
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 40 == 0) {
			boolean x = false;
			if (stack != null && master != null) {
				TileMaster mas = ((TileMaster) worldObj.getTileEntity(master));
				int num = mas.getAmount(new FilterItem(stack.getStack()));
				if (more) {
					if (num > stack.getSize())
						x = true;
					else
						x = false;
				} else {
					if (num <= stack.getSize())
						x = true;
					else
						x = false;
				}
			}
			if (worldObj.getBlockState(pos).getValue(BlockIndicator.STATE) != x) {
				((BlockIndicator) worldObj.getBlockState(pos).getBlock()).setState(worldObj, pos, worldObj.getBlockState(pos), x);
				Util.updateTile(worldObj, pos);
			}
		}
	}
}
