package mrriegel.storagenetwork.helper;

import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class CraftingTask {
	int id;
	ItemStack output;
	int outputSize;
	int done;
	BlockPos machine;

	public CraftingTask() {
	}

	public CraftingTask(int id, ItemStack output, int outputSize, int done, BlockPos machine) {
		this.id = id;
		this.output = output;
		this.outputSize = outputSize;
		this.done = done;
		this.machine = machine;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CraftingTask))
			return false;
		return ((CraftingTask) obj).id == id;
	}

	public static int newID(List<CraftingTask> tasks) {
		Random ran = new Random(0x440044 << 2 | 22 & Short.MAX_VALUE);
		int i = ran.nextInt();
		while (true) {
			for (CraftingTask t : tasks) {
				if (t.id == i) {
					i = ran.nextInt();
					continue;
				}
			}
			break;
		}
		return i;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("craftingID", id);
		NBTTagCompound c = new NBTTagCompound();
		output.writeToNBT(c);
		nbt.setTag("outputStack", c);
		nbt.setInteger("outputSize", outputSize);
		nbt.setInteger("done", done);
		if (machine != null)
			nbt.setLong("machinePos", machine.toLong());
		else
			nbt.setLong("machinePos", Long.MAX_VALUE);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		id = nbt.getInteger("craftingID");
		output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("outputStack"));
		outputSize = nbt.getInteger("outputSize");
		done = nbt.getInteger("done");
		machine = nbt.getLong("machinePos") == Long.MAX_VALUE ? null : BlockPos.fromLong(nbt.getLong("machinePos"));
	}

}
