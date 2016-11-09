package mrriegel.storagenetwork.tile;

import cofh.api.energy.IEnergyReceiver;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.EnergyStorageExt;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.ModConfig;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.network.InventoryNetworkPart;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * @author canitzp
 */
public class TileNetworkCore extends CommonTile implements ITickable,IEnergyReceiver{

	public Network network;
	protected boolean needsUpdate;
	protected EnergyStorageExt energy =new EnergyStorageExt(200000, 2000);

	public void initializeNetwork() {
		this.network = new Network();
		network.corePosition = new GlobalBlockPos(pos, worldObj);
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
//		player.openGui(StorageNetwork.instance, GuiID.NETWORK_CORE.ordinal(), worldObj, getX(), getY(), getZ());
		return false;
	}

	@Override
	public void update() {
		if ((needsUpdate || network == null) && onServer()) {
			initializeNetwork();
			needsUpdate = false;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		energy.setEnergyStored(NBTHelper.getInt(compound, "energy"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setInt(compound, "energy", energy.getEnergyStored());
		return super.writeToNBT(compound);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (ModConfig.needsEnergy&&capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ModConfig.needsEnergy&&capability == CapabilityEnergy.ENERGY)
			return (T) energy;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		markForSync();
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return ModConfig.needsEnergy;
	}

}
