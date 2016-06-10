package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.config.ConfigHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileFluidBox extends AbstractFilterTile {

	private FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * ConfigHandler.fluidBoxCapacity);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readTank(compound);
	}

	public void readTank(NBTTagCompound compound) {
		tank.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeTank(compound);
		return compound;
	}

	public void writeTank(NBTTagCompound compound) {
		tank.writeToNBT(compound);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public IFluidHandler getFluidTank() {
		return new IFluidHandler() {

			@Override
			public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
				return tank.fill(resource, doFill);
			}

			@Override
			public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
				if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
					return null;
				}
				return tank.drain(resource.amount, doDrain);
			}

			@Override
			public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
				return tank.drain(maxDrain, doDrain);
			}

			@Override
			public boolean canFill(EnumFacing from, Fluid fluid) {
				return true;
			}

			@Override
			public boolean canDrain(EnumFacing from, Fluid fluid) {
				return true;
			}

			@Override
			public FluidTankInfo[] getTankInfo(EnumFacing from) {
				return new FluidTankInfo[] { tank.getInfo() };
			}
		};
	}

	@Override
	public IInventory getInventory() {
		return null;
	}

	@Override
	public BlockPos getSource() {
		return pos;
	}

	@Override
	public boolean isFluid() {
		return true;
	}

	@Override
	public boolean isStorage() {
		return true;
	}

}