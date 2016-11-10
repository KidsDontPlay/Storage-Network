package mrriegel.storagenetwork.tile;

import java.util.Set;

import com.google.common.collect.Sets;

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
import mrriegel.storagenetwork.network.InventoryNetworkPart;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * @author canitzp
 */
public class TileNetworkCore extends CommonTile implements ITickable, IEnergyReceiver {

	public Network network;
	protected boolean needsUpdate;
	protected EnergyStorageExt energy = new EnergyStorageExt(200000, 2000) {
		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (worldObj.getTotalWorldTime() % 4 == 0)
				markForSync();
			if ((double) getEnergyStored() / (double) getMaxEnergyStored() > 0.75)
				return super.extractEnergy(Math.min(maxExtract, getMaxEnergyStored() / 10), simulate);
			return 0;
		};
	};

	public void initializeNetwork() {
		this.network = new Network();
		network.corePosition = new GlobalBlockPos(pos, worldObj);
		runThroughNetwork(pos);
		System.out.println("network size: " + network.networkParts.size());
	}

	private void runThroughNetwork(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos searchPos = pos.offset(facing);
			if (worldObj.getTileEntity(pos) instanceof INetworkPart && !((INetworkPart) worldObj.getTileEntity(pos)).getNeighborFaces().contains(facing))
				continue;
			if (worldObj.getTileEntity(searchPos) instanceof INetworkPart && !((INetworkPart) worldObj.getTileEntity(searchPos)).getNeighborFaces().contains(facing.getOpposite()))
				continue;
			if (!getWorld().isAirBlock(searchPos)) {
				TileEntity tile = getWorld().getTileEntity(searchPos);
				if (tile != null) {
					if (tile instanceof TileNetworkCore && !tile.getPos().equals(this.pos)) {
						worldObj.setBlockToAir(searchPos);
						worldObj.playEvent(2001, searchPos, Block.getIdFromBlock(Registry.networkCore));
						StackHelper.spawnItemStack(worldObj, searchPos, new ItemStack(Registry.networkCore));
						markForNetworkInit();
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

	public void markForNetworkInit() {
		needsUpdate = true;
	}

	@Override
	public void update() {
		if (worldObj.getTotalWorldTime() + (pos.hashCode() % 80) % (network == null ? 80 : 300) == 0) {
			needsUpdate = true;
		}
		if ((needsUpdate || network == null) && onServer()) {
			needsUpdate = false;
			try {
				initializeNetwork();
			} catch (StackOverflowError error) {
				StorageNetwork.logger.error("Couldn't build the network due to a StackOverflowError.");
			}
		}
		if (onServer() && network != null)
			distributeEnergy();
	}

	private void distributeEnergy() {
		for (INetworkPart part : network.networkParts)
			if (part instanceof TileNetworkEnergyInterface) {
				TileNetworkEnergyInterface tile = (TileNetworkEnergyInterface) part;
				boolean simulate = false;
				if (tile.getTile() instanceof IEnergyReceiver) {
					int maxReceive = ((IEnergyReceiver) tile.getTile()).receiveEnergy(tile.tileFace.getOpposite(), 1000, true);
					((IEnergyReceiver) tile.getTile()).receiveEnergy(tile.tileFace.getOpposite(), energy.extractEnergy(maxReceive, simulate), simulate);
				} else if (tile.getTile() != null && tile.getTile().hasCapability(CapabilityEnergy.ENERGY, tile.tileFace.getOpposite())) {
					int maxReceive = tile.getTile().getCapability(CapabilityEnergy.ENERGY, tile.tileFace.getOpposite()).receiveEnergy(1000, true);
					tile.getTile().getCapability(CapabilityEnergy.ENERGY, tile.tileFace.getOpposite()).receiveEnergy(energy.extractEnergy(maxReceive, simulate), simulate);
				}
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
		return (ModConfig.needsEnergy && capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ModConfig.needsEnergy && capability == CapabilityEnergy.ENERGY)
			return (T) energy;
		return super.getCapability(capability, facing);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (worldObj.getTotalWorldTime() % 4 == 0)
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
