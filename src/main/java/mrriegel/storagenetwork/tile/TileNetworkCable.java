package mrriegel.storagenetwork.tile;

import java.util.Map;

import mrriegel.limelib.tile.CommonTile;
import net.minecraft.util.EnumFacing;

import com.google.common.collect.Maps;

public class TileNetworkCable extends CommonTile{
	
	protected Map<EnumFacing, Boolean> valids = Maps.newHashMap();
	
	{
		for (EnumFacing f : EnumFacing.VALUES) {
			valids.put(f, true);
		}
	}

	public Map<EnumFacing, Boolean> getValids() {
		return valids;
	}

}
