package mrriegel.storagenetwork.tile;

import mrriegel.limelib.util.StackWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileItemMirror extends TileNetworkPart implements ITickable{

	public StackWrapper topLeft, topRight, bottomLeft, bottomRight;
	public EnumFacing face = EnumFacing.DOWN;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		topLeft = StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag("topLeft"));
		topRight = StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag("topRight"));
		bottomLeft = StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag("bottomLeft"));
		bottomRight = StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag("bottomRight"));
		face = EnumFacing.VALUES[compound.getInteger("face")];
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (topLeft != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			topLeft.writeToNBT(nbt);
			compound.setTag("topLeft", nbt);
		}
		if (topRight != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			topRight.writeToNBT(nbt);
			compound.setTag("topRight", nbt);
		}
		if (bottomLeft != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			bottomLeft.writeToNBT(nbt);
			compound.setTag("bottomLeft", nbt);
		}
		if (bottomRight != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			bottomRight.writeToNBT(nbt);
			compound.setTag("bottomRight", nbt);
		}
		compound.setInteger("face", face.ordinal());
		return super.writeToNBT(compound);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
