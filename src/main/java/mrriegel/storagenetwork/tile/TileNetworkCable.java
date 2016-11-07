package mrriegel.storagenetwork.tile;

import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

public class TileNetworkCable extends CommonTile implements INetworkPart{
	
	private Map<EnumFacing, Boolean> validSides = new HashMap<>();
	
	public TileNetworkCable(){
		for (EnumFacing f : EnumFacing.VALUES) {
			validSides.put(f, true);
		}
	}

	@Nonnull
	public Map<EnumFacing, Boolean> getValidSides() {
		return validSides;
	}

	public void setSide(EnumFacing side, boolean state){
		this.validSides.put(side, state);
		this.markForSync();
	}

	public boolean isSideValid(EnumFacing side){
		return this.validSides.get(side);
	}

	@Override
	public GlobalBlockPos getPosition() {
		return new GlobalBlockPos(pos, worldObj);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		for (EnumFacing f : EnumFacing.VALUES) {
			validSides.put(f, compound.hasKey(f.toString() + "valid") ? compound.getBoolean(f.toString() + "valid") : true);
		}
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for (EnumFacing f : EnumFacing.VALUES) {
			compound.setBoolean(f.toString() + "valid", validSides.get(f));
		}
		return super.writeToNBT(compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		worldObj.markBlockRangeForRenderUpdate(pos.add(1, 1, 1), pos.add(-1, -1, -1));
	}
	

}
