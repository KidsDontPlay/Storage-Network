package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.blocks.BlockAnnexer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileFannexer extends TileEntity implements IConnectable, ITickable {

	private BlockPos master;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		return compound;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public BlockPos getMaster() {
		return master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
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
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0 && master != null && worldObj.getTileEntity(master) instanceof TileMaster && !worldObj.isBlockPowered(pos)) {
			BlockPos p = pos.offset(worldObj.getBlockState(pos).getValue(BlockAnnexer.FACING).getOpposite());
			IBlockState state = worldObj.getBlockState(p);
			if (drainBlock(state, worldObj, p, false) == null)
				return;
			TileMaster mas = (TileMaster) worldObj.getTileEntity(master);
			FluidStack fluid = drainBlock(state, worldObj, p, false);
			if (mas.insertFluid(fluid, null, true) > 0)
				return;
			if (!mas.consumeRF(1000, false))
				return;
			int rest = mas.insertFluid(fluid, null, false);
			if (rest > 0)
				mas.frequest(fluid.getFluid(), fluid.amount - rest, false);
			else {
				drainBlock(state, worldObj, p, true);
			}

		}
	}

	private FluidStack drainBlock(IBlockState state, World world, BlockPos pos, boolean doDrain) {
		Block block = state.getBlock();
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

		if (fluid != null && FluidRegistry.isFluidRegistered(fluid)) {
			if (block instanceof IFluidBlock) {
				IFluidBlock fluidBlock = (IFluidBlock) block;
				if (!fluidBlock.canDrain(world, pos)) {
					return null;
				}
				return fluidBlock.drain(world, pos, doDrain);
			} else {
				int level = state.getValue(BlockLiquid.LEVEL);
				if (level != 0) {
					return null;
				}

				if (doDrain) {
					world.setBlockToAir(pos);
				}

				return new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
			}
		} else {
			return null;
		}
	}
}
