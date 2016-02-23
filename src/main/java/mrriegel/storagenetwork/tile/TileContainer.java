package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.api.ITemplateContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class TileContainer extends CrunchTEInventory implements IConnectable, ITemplateContainer {
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

	@Override
	public List<ItemStack> getTemplates() {
		List<ItemStack> lis = new ArrayList<ItemStack>();
		for (int i = 0; i < INVSIZE; i++)
			if (getStackInSlot(i) != null)
				lis.add(getStackInSlot(i).copy());
		return lis;
	}

}
