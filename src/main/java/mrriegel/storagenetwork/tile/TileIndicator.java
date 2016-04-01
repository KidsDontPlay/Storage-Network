package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.blocks.BlockIndicator;
import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.StackWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileIndicator extends TileEntity implements IConnectable, ITickable {

	private boolean more;
	private StackWrapper stack;
	private BlockPos master;
	private boolean disabled;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		more = compound.getBoolean("more");
		if (compound.hasKey("stack", 10))
			stack = (StackWrapper.loadStackWrapperFromNBT(compound.getCompoundTag("stack")));
		else
			stack = null;
		disabled=compound.getBoolean("disabled");

	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		compound.setBoolean("more", more);
		if (stack != null)
			compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
		compound.setBoolean("disabled", disabled);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	public boolean isMore() {
		return more;
	}

	public void setMore(boolean more) {
		this.more = more;
	}

	public StackWrapper getStack() {
		return stack;
	}

	public void setStack(StackWrapper stack) {
		this.stack = stack;
	}

	@Override
	public BlockPos getMaster() {
		return master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
	}
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean enabled) {
		this.disabled = enabled;
	}
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);

		return new S35PacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 40 == 0) {
			// for(int y=pos.getY();y>0;y--){
			// BlockPos p=new BlockPos(pos.getX(), y, pos.getZ());
			// IFluidBlock ff;
			// Fluid
			// f=FluidRegistry.lookupFluidForBlock(worldObj.getBlockState(p).getBlock());
			// if(f!=null){
			// System.out.println(f.getUnlocalizedName());
			// worldObj.setBlockToAir(p);
			// }
			// }
			boolean x = false;
			if (stack != null && master != null) {
				TileMaster mas = ((TileMaster) worldObj.getTileEntity(master));
				int num = mas.getAmount(new FilterItem(stack.getStack(), true, false));
				if (more) {
					if (num > stack.getSize())
						x = true;
					else
						x = false;
				} else {
					if (num <= stack.getSize())
						x = true;
					else
						x = false;
				}
			}
			if (worldObj.getBlockState(pos).getValue(BlockIndicator.STATE) != x) {
				((BlockIndicator) worldObj.getBlockState(pos).getBlock()).setState(worldObj, pos, worldObj.getBlockState(pos), x);
				worldObj.markBlockForUpdate(pos);
			}
		}
	}
}
