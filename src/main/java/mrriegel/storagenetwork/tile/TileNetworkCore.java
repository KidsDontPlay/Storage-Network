package mrriegel.storagenetwork.tile;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.network.InventoryNetworkPart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * @author canitzp
 */
public class TileNetworkCore extends CommonTile{

	public Network network;

	public void initializeNetwork() {
		Network network = new Network();
		network.corePosition = new GlobalBlockPos(pos, worldObj.provider.getDimension());
		this.network = network;
		runThroughNetwork(pos);
		System.out.println("network size: "+network.networkParts.size());
	}
	
	private void runThroughNetwork(BlockPos pos){
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos searchPos = pos.offset(facing);
			if(worldObj.getTileEntity(pos) instanceof TileNetworkCable&&!((TileNetworkCable)worldObj.getTileEntity(pos)).getValidSides().get(facing))
				continue;
			if (!getWorld().isAirBlock(searchPos)) {
				TileEntity tile = getWorld().getTileEntity(searchPos);
				if (tile != null) {
					if (tile instanceof INetworkPart&&!network.networkParts.contains(tile)) {
						network.addPart((INetworkPart) tile);
					} else if (InvHelper.hasItemHandler(tile, facing.getOpposite())&&!network.networkParts.contains(InvHelper.getItemHandler(tile, facing.getOpposite()))) {
						network.addPart(new InventoryNetworkPart(tile.getWorld(), searchPos, InvHelper.getItemHandler(tile, facing.getOpposite())));
					} else
						continue;
					runThroughNetwork(searchPos);
				}
			}
		}
	}
	
	@Override
	public boolean openGUI(EntityPlayerMP player) {
		// TODO open gui
		return super.openGUI(player);
	}

}
