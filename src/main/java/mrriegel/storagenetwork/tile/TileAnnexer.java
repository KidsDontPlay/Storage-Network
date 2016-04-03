package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.blocks.BlockAnnexer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileAnnexer extends TileEntity implements IConnectable, ITickable {

	private BlockPos master;
	private boolean disabled;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		disabled = compound.getBoolean("disabled");

	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		compound.setBoolean("disabled", disabled);
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
	public boolean isDisabled() {
		return disabled;
	}

	@Override
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
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0 && master != null && worldObj.getTileEntity(master) instanceof TileMaster) {
			BlockPos p = pos.offset(worldObj.getBlockState(pos).getValue(BlockAnnexer.FACING).getOpposite());
			Block block = worldObj.getBlockState(p).getBlock();
			if (!canBreakBlock(block, p))
				return;
			List<ItemStack> lis = block.getDrops(worldObj, p, worldObj.getBlockState(p), 0);
			TileMaster mas = (TileMaster) worldObj.getTileEntity(master);
			if (!mas.consumeRF((int) (block.getBlockHardness(worldObj, p) * 5f), false))
				return;
			worldObj.playAuxSFX(2001, p, Block.getStateId(worldObj.getBlockState(p)));
			worldObj.setBlockToAir(p);
			if (worldObj.getTileEntity(p) != null)
				worldObj.removeTileEntity(p);
			block.dropXpOnBlockBreak(worldObj, p, block.getExpDrop(worldObj, p, 0));
			for (ItemStack s : lis) {
				int rest = mas.insertStack(s, null);
				if (rest > 0) {
					ItemStack spawn = s.copy();
					spawn.stackSize = rest;
					spawnItemStack(worldObj, p, spawn);
				}
			}
		}
	}

	public void spawnItemStack(World worldIn, BlockPos pos, ItemStack stack) {
		if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) {
			float f = 0.5F;
			double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
			entityitem.setDefaultPickupDelay();
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

	private boolean canBreakBlock(Block block, BlockPos pos) {
		return !block.isAir(worldObj, pos) && !block.getMaterial().isLiquid() && block != Blocks.bedrock && block.getBlockHardness(worldObj, pos) > -1.0F && block.getHarvestLevel(worldObj.getBlockState(pos)) <= 3;
	}
}
