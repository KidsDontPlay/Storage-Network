package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileFRequest extends TileConnectable {
	public ItemStack fill, drain;
	public boolean downwards;
	public Sort sort = Sort.NAME;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("dir", downwards);
		compound.setString("sort", sort.toString());
		if (fill != null)
			compound.setTag("fill", fill.writeToNBT(new NBTTagCompound()));
		if (drain != null)
			compound.setTag("drain", drain.writeToNBT(new NBTTagCompound()));
		return compound;
	}
}