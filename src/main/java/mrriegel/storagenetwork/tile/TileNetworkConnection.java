package mrriegel.storagenetwork.tile;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TileNetworkConnection extends TileNetworkCable {

	public EnumFacing tileFace = EnumFacing.DOWN;

	public TileEntity getTile() {
		return worldObj.getTileEntity(pos.offset(tileFace));
	}

	public <T> T getCapability(Capability<T> capa) {
		if (getTile() == null || !getTile().hasCapability(capa, tileFace.getOpposite()))
			return null;
		return getTile().getCapability(capa, tileFace.getOpposite());
	}
	
	@Override
	public Map<EnumFacing, Boolean> getValidSides() {
		Map<EnumFacing, Boolean> map = super.getValidSides();
		map.put(tileFace, false);
		return map;
	}

	@Override
	public void setSide(EnumFacing side, boolean state) {
		if (side != tileFace)
			super.setSide(side, state);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		tileFace = EnumFacing.VALUES[compound.getInteger("face")];
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("face", tileFace.ordinal());
		return super.writeToNBT(compound);
	}

}
