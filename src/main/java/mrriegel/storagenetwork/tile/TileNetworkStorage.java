package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.Enums.IOMODE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileNetworkStorage extends TileNetworkItemConnection {

	public IOMODE iomode = IOMODE.INOUT;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		iomode = IOMODE.values()[compound.getInteger("iomode")];
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("iomode", iomode.ordinal());
		return super.writeToNBT(compound);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		System.out.println("handler");
		iomode = iomode.next();
	}

}
