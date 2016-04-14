package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.List;

import mrriegel.storagenetwork.api.IConnectable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileContainer extends CrunchTEInventory implements IConnectable {
	private BlockPos master;
	private EnumFacing input, output;

	public TileContainer() {
		super(9);
		input = EnumFacing.UP;
		output = EnumFacing.DOWN;
	}

	@Override
	protected void readSyncableDataFromNBT(NBTTagCompound tag) {
		master = new Gson().fromJson(tag.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		input = EnumFacing.byName(tag.getString("input"));
		input = input != null ? input : EnumFacing.UP;
		output = EnumFacing.byName(tag.getString("output"));
		output = output != null ? output : EnumFacing.DOWN;
	}

	@Override
	protected void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setString("master", new Gson().toJson(master));
		if (input != null)
			tag.setString("input", input.toString());
		if (output != null)
			tag.setString("output", output.toString());
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

	public EnumFacing getInput() {
		return input;
	}

	public void setInput(EnumFacing input) {
		this.input = input;
	}

	public EnumFacing getOutput() {
		return output;
	}

	public void setOutput(EnumFacing output) {
		this.output = output;
	}

	public List<ItemStack> getTemplates() {
		List<ItemStack> lis = new ArrayList<ItemStack>();
		for (int i = 0; i < INVSIZE; i++)
			if (getStackInSlot(i) != null)
				lis.add(getStackInSlot(i).copy());
		return lis;
	}

	@Override
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

}
