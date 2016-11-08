package mrriegel.storagenetwork.tile;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.network.InventoryNetworkPart;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

/**
 * @author canitzp
 */
public class TileNetworkCore extends CommonTile implements ITickable{

	public Network network;
	protected boolean needsUpdate;

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
					if(tile instanceof TileNetworkCore&&!tile.getPos().equals(this.pos)){
						worldObj.setBlockToAir(searchPos);
						worldObj.playEvent(2001, searchPos, Block.getIdFromBlock(Registry.networkCore));
						StackHelper.spawnItemStack(worldObj, searchPos, new ItemStack(Registry.networkCore));
					} else if (tile instanceof INetworkPart && !network.networkParts.contains(tile)) {
						network.addPart((INetworkPart) tile);
					} else if (InvHelper.hasItemHandler(tile, facing.getOpposite()) && !network.networkParts.contains(InvHelper.getItemHandler(tile, facing.getOpposite()))) {
						network.addPart(new InventoryNetworkPart(tile.getWorld(), searchPos, InvHelper.getItemHandler(tile, facing.getOpposite())));
					} else
						continue;
					runThroughNetwork(searchPos);
				}
			}
		}
	}
	
	public void markForNetworkInit(){
		needsUpdate=true;
	}
	
	@Override
	public boolean openGUI(EntityPlayerMP player) {
		// TODO open gui
		return super.openGUI(player);
	}

	@Override
	public void update() {
		if ((needsUpdate || network == null) && onServer()) {
			initializeNetwork();
			needsUpdate = false;
		}
	}

}
