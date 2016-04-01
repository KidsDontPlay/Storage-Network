package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileFRequest extends TileEntity implements IConnectable {
	private BlockPos master;
	public ItemStack fill, drain;
	public boolean downwards;
	public Sort sort = Sort.NAME;
	private boolean disabled;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		downwards = compound.getBoolean("dir");
		sort = Sort.valueOf(compound.getString("sort"));
		if (compound.hasKey("fill", 10))
			fill = (ItemStack.loadItemStackFromNBT(compound.getCompoundTag("fill")));
		else
			fill = null;
		if (compound.hasKey("drain", 10))
			drain = (ItemStack.loadItemStackFromNBT(compound.getCompoundTag("drain")));
		else
			drain = null;
		disabled = compound.getBoolean("disabled");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		compound.setBoolean("dir", downwards);
		compound.setString("sort", sort.toString());
		if (fill != null)
			compound.setTag("fill", fill.writeToNBT(new NBTTagCompound()));
		if (drain != null)
			compound.setTag("drain", drain.writeToNBT(new NBTTagCompound()));
		compound.setBoolean("disabled", disabled);
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
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}
}