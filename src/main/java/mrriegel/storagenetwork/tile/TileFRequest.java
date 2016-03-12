package mrriegel.storagenetwork.tile;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.StacksMessage;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.util.Constants;

public class TileFRequest extends TileEntity implements IConnectable {
	private BlockPos master;
	public ItemStack fill, drain;
	public boolean downwards;
	public Sort sort = Sort.NAME;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		downwards = compound.getBoolean("dir");
		sort = Sort.valueOf(compound.getString("sort"));
		ItemStack f = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("fill"));
		if (f != null && f.getItem() == null)
			f = null;
		ItemStack d = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("drain"));
		if (d != null && d.getItem() == null)
			d = null;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		compound.setBoolean("dir", downwards);
		compound.setString("sort", sort.toString());
		NBTTagCompound f = new NBTTagCompound();
		if (fill == null)
			new ItemStack((Block) null).writeToNBT(f);
		else
			fill.writeToNBT(f);
		compound.setTag("fill", f);
		NBTTagCompound d = new NBTTagCompound();
		if (drain == null)
			new ItemStack((Block) null).writeToNBT(d);
		else
			drain.writeToNBT(d);
		compound.setTag("drain", d);
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
}