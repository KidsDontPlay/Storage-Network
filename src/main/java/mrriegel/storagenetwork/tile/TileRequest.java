package mrriegel.storagenetwork.tile;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class TileRequest extends TileConnectable {
	public Map<Integer, ItemStack> matrix = new HashMap<Integer, ItemStack>();
	public boolean downwards;
	public Sort sort = Sort.NAME;

	public enum Sort {
		AMOUNT, NAME, MOD;
		private static Sort[] vals = values();

		public Sort next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		downwards = compound.getBoolean("dir");
		sort = Sort.valueOf(compound.getString("sort"));
		NBTTagList invList = compound.getTagList("matrix", Constants.NBT.TAG_COMPOUND);
		matrix = new HashMap<Integer, ItemStack>();
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			matrix.put(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("dir", downwards);
		compound.setString("sort", sort.toString());
		NBTTagList invList = new NBTTagList();
		invList = new NBTTagList();
		for (int i = 0; i < 9; i++) {
			if (matrix.get(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				matrix.get(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		compound.setTag("matrix", invList);
		return compound;
	}
}
