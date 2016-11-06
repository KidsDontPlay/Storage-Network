package mrriegel.storagenetwork.tile;

import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.GlobalBlockPos;
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

}
