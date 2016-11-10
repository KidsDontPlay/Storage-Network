package mrriegel.storagenetwork.tile;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;

import com.google.common.collect.Sets;

public class TileNetworkCable extends TileNetworkPart{
	
	protected Map<EnumFacing, Boolean> validSides = new HashMap<>();
	
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
		this.getValidSides().put(side, state);
		this.markForSync();
	}

	public boolean isSideValid(EnumFacing side){
		return this.getValidSides().get(side);
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

	@Override
	public EnumSet<EnumFacing> getNeighborFaces() {
		Set<EnumFacing> set=Sets.newHashSet();
		for (EnumFacing f : EnumFacing.VALUES) {
			if(getValidSides().get(f))
				set.add(f);
		}
		return EnumSet.<EnumFacing>copyOf(set);
	}
	

}
