package mrriegel.storagenetwork.tile;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.CombinedEnergyStorageExt;
import mrriegel.limelib.util.EnergyStorageExt;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.ModConfig;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.block.BlockNetworkCore;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import cofh.api.energy.IEnergyReceiver;

/**
 * @author canitzp
 */
public class TileNetworkCore extends CommonTile implements ITickable, IEnergyReceiver {

	public Network network;
	protected boolean needsUpdate;
	//	protected EnergyStorageExt energy = new EnergyStorageExt(200000, 1000) {
	//		@Override
	//		public int extractEnergy(int maxExtract, boolean simulate) {
	//			if (worldObj.getTotalWorldTime() % 4 == 0)
	//				markForSync();
	//			if ((double) getEnergyStored() / (double) getMaxEnergyStored() > 0.75)
	//				return super.extractEnergy(Math.min(maxExtract, getMaxEnergyStored() / 10), simulate);
	//			return 0;
	//		};
	//	};

	protected EnergyStorageExt receiver = new EnergyStorageExt(100000, 1000) {
	};

	protected EnergyStorageExt extractor = new EnergyStorageExt(100000, 1000) {
		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (worldObj.getTotalWorldTime() % 4 == 0)
				markForSync();
			if ((double) getEnergyStorage().getEnergyStored() / (double) getEnergyStorage().getMaxEnergyStored() > 0.5)
				return super.extractEnergy(Math.min(maxExtract, getMaxEnergyStored() / 10), simulate);
			return 0;
		};
	};

	public void initializeNetwork() {
		this.network = new Network();
		network.corePosition = new GlobalBlockPos(pos, worldObj);
		try {
			runThroughNetwork(pos);
		} catch (StackOverflowError error) {
			StorageNetwork.logger.error("Couldn't build the network due to a StackOverflowError.");
		}
		System.out.println("network size: " + network.networkParts.size());
	}

	private void runThroughNetwork(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos searchPos = pos.offset(facing);
			if (worldObj.getTileEntity(pos) instanceof IToggleable && !((IToggleable) worldObj.getTileEntity(pos)).isActive())
				return;
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
//		System.out.println("kill "+(worldObj.getTotalWorldTime() + (pos.hashCode() % 300)) % (network == null ? 80 : 300));
		if ((worldObj.getTotalWorldTime() + (pos.hashCode() % 300)) % (network == null ? 80 : 300) == 0) {
			needsUpdate = true;
			//Lag
			needsUpdate=false;
		}
		if ((needsUpdate || network == null) && onServer()) {
			needsUpdate = false;
			initializeNetwork();
		}
		if (onServer() && network != null) {
			if (ModConfig.needsEnergy && worldObj.getTotalWorldTime() % 15 == 0) {
				if (getEnergyStorage().getEnergyStored() == 0 && worldObj.getBlockState(pos).getValue(BlockNetworkCore.ACTIVE))
					worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockNetworkCore.ACTIVE, false), 2);
				else if (getEnergyStorage().getEnergyStored() > 0 && !worldObj.getBlockState(pos).getValue(BlockNetworkCore.ACTIVE))
					worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockNetworkCore.ACTIVE, true), 2);
			}
			distributeEnergy();
			network.importItems();
			network.exportItems();
		}
	}

	private void distributeEnergy() {
		if (worldObj.getTotalWorldTime() % 20 == 0) {
			extractor.setMaxExtract(getTotalTransfer());
			extractor.setMaxReceive(getTotalTransfer());
			receiver.setMaxExtract(getTotalTransfer());
			receiver.setMaxReceive(getTotalTransfer());
		}
		int maxTransfer = getTotalTransfer();
		for (INetworkPart part : network.networkParts)
			if (part instanceof TileNetworkEnergyInterface) {
				TileNetworkEnergyInterface tile = (TileNetworkEnergyInterface) part;
				boolean simulate = false;
				if (tile.getTile() instanceof IEnergyReceiver && tile.iomode.canExtract()) {
					int maxReceive = ((IEnergyReceiver) tile.getTile()).receiveEnergy(tile.tileFace.getOpposite(), maxTransfer, true);
					((IEnergyReceiver) tile.getTile()).receiveEnergy(tile.tileFace.getOpposite(), extractor.extractEnergy(maxReceive, simulate), simulate);
				} else if (tile.getTile() != null && tile.getTile().hasCapability(CapabilityEnergy.ENERGY, tile.tileFace.getOpposite()) && tile.iomode.canExtract()) {
					int maxReceive = tile.getTile().getCapability(CapabilityEnergy.ENERGY, tile.tileFace.getOpposite()).receiveEnergy(maxTransfer, true);
					tile.getTile().getCapability(CapabilityEnergy.ENERGY, tile.tileFace.getOpposite()).receiveEnergy(extractor.extractEnergy(maxReceive, simulate), simulate);
				}
			}
		for (INetworkPart part : network.networkParts)
			if (part instanceof TileNetworkEnergyCell) {
				if (getEnergyStored(null) != getMaxEnergyStored(null)) {
					int maxReceive = receiveEnergy(null, maxTransfer, true);
					receiveEnergy(null, ((TileNetworkEnergyCell) part).getEnergy().extractEnergy(maxReceive, false), false);
				} else if ((double) getEnergyStored(null) / (double) getMaxEnergyStored(null) > .6) {
					int maxReceive = ((TileNetworkEnergyCell) part).getEnergy().receiveEnergy(maxTransfer, true);
					((TileNetworkEnergyCell) part).getEnergy().receiveEnergy(getEnergyStorage().extractEnergy(maxReceive, false), false);
				}
			}
	}

	public int getTotalTransfer() {
		int max = 1000;
		for (INetworkPart part : network.networkParts)
			if (part instanceof TileNetworkEnergyCell)
				max += 1000;
		return max;
	}

	public int getTotalEnergy() {
		int max = getEnergyStored(null);
		for (INetworkPart part : network.networkParts)
			if (part instanceof TileNetworkEnergyCell)
				max += ((TileNetworkEnergyCell) part).getEnergy().getEnergyStored();
		return max;
	}

	public int getTotalMaxEnergy() {
		int max = getMaxEnergyStored(null);
		for (INetworkPart part : network.networkParts)
			if (part instanceof TileNetworkEnergyCell)
				max += ((TileNetworkEnergyCell) part).getEnergy().getMaxEnergyStored();
		return max;
	}

	public IEnergyStorage getEnergyStorage() {
		return new CombinedEnergyStorageExt(receiver, extractor);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		receiver.setEnergyStored(NBTHelper.getInt(compound, "receiver"));
		extractor.setEnergyStored(NBTHelper.getInt(compound, "extractor"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setInt(compound, "receiver", receiver.getEnergyStored());
		NBTHelper.setInt(compound, "extractor", extractor.getEnergyStored());
		return super.writeToNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (ModConfig.needsEnergy && capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ModConfig.needsEnergy && capability == CapabilityEnergy.ENERGY)
			return (T) getEnergyStorage();
		return super.getCapability(capability, facing);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (worldObj.getTotalWorldTime() % 4 == 0)
			markForSync();
		return getEnergyStorage().receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return getEnergyStorage().getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return getEnergyStorage().getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return ModConfig.needsEnergy;
	}

}
