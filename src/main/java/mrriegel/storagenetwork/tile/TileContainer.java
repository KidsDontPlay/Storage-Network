package mrriegel.storagenetwork.tile;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mrriegel.storagenetwork.api.IConnectable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class TileContainer extends CrunchTEInventory implements IConnectable {
	private BlockPos master;

	public TileContainer() {
		super(9);
	}

	@Override
	protected void readSyncableDataFromNBT(NBTTagCompound tag) {
		master = new Gson().fromJson(tag.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
	}

	@Override
	protected void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setString("master", new Gson().toJson(master));
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public BlockPos getMaster() {
		return this.master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
	}

}
