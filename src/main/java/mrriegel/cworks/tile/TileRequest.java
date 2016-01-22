package mrriegel.cworks.tile;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

public class TileRequest extends TileEntity implements ITickable {
	private BlockPos master;
	private Map<Integer, ItemStack> back = new HashMap<Integer, ItemStack>();

	@Override
	public void update() {

	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"),
				new TypeToken<BlockPos>() {
				}.getType());
		NBTTagList invList = compound.getTagList("crunchTE",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < 15; i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			back.put(i, ItemStack.loadItemStackFromNBT(stackTag));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < 15; i++) {
			if (back.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				back.get(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		compound.setTag("crunchTE", invList);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(new BlockPos(this.getPos().getX(),
				this.getPos().getY(), this.getPos().getZ()), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public BlockPos getMaster() {
		return master;
	}

	public void setMaster(BlockPos master) {
		this.master = master;
	}
}
