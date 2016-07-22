package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.config.ConfigHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class TileFluidBox extends AbstractFilterTile {

	private FluidTank tank = new FluidTank(Fluid.BUCKET_VOLUME * ConfigHandler.fluidBoxCapacity);

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
	public IFluidHandler getFluidTank() {
		return tank;
	}

	@Override
	public IItemHandler getInventory() {
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