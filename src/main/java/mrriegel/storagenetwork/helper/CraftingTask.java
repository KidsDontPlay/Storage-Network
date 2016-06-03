package mrriegel.storagenetwork.helper;

import java.util.List;
import java.util.Map;
import java.util.Random;

import mrriegel.storagenetwork.items.ItemTemplate;
import mrriegel.storagenetwork.tile.TileContainer;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class CraftingTask {
	CraftingTask parent = null;
	private int id;
	private ItemStack output;
	private int outputSize;
	private int done, insertDone;
	private boolean process;
	List<CraftingTask> children = Lists.newArrayList();

	// private BlockPos machine;

	private CraftingTask() {
	}

	public CraftingTask(int id, ItemStack output, int outputSize, boolean process) {
		this.id = id;
		this.output = output;
		this.outputSize = outputSize;
		this.process = process;
		// this.machine = machine;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CraftingTask))
			return false;
		return ((CraftingTask) obj).id == id;
	}

	public static int newID(List<CraftingTask> tasks) {
		Random ran = new Random((0x440044 << 2) | (22 & 37 >>> Short.MIN_VALUE));
		int i = 0;
		while (true) {
			for (CraftingTask t : tasks) {
				if (t.id == i) {
					i = Math.abs(ran.nextInt(Short.MAX_VALUE));
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
		nbt.setBoolean("process", process);
		// if (machine != null)
		// nbt.setLong("machinePos", machine.toLong());
		// else
		// nbt.setLong("machinePos", Long.MAX_VALUE);
	}

	public boolean progress(TileMaster tile) {
		List<ItemStack> templates = tile.getTemplates(new FilterItem(output));
		for (ItemStack template : templates) {
			Map<Integer, ItemStack> ing = ItemTemplate.getInput(template);
			TileContainer con = (TileContainer) tile.getWorld().getTileEntity(ItemTemplate.getPos(template));
		}
		return false;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		id = nbt.getInteger("craftingID");
		output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("outputStack"));
		outputSize = nbt.getInteger("outputSize");
		done = nbt.getInteger("done");
		process = nbt.getBoolean("process");
		// machine = nbt.getLong("machinePos") == Long.MAX_VALUE ? null :
		// BlockPos.fromLong(nbt.getLong("machinePos"));
	}

	public static CraftingTask loadCraftingTaskFromNBT(NBTTagCompound nbt) {
		CraftingTask fil = new CraftingTask();
		fil.readFromNBT(nbt);
		return fil;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ItemStack getOutput() {
		return output;
	}

	public void setOutput(ItemStack output) {
		this.output = output;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public void setOutputSize(int outputSize) {
		this.outputSize = outputSize;
	}

	public int getDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

	public boolean isProcess() {
		return process;
	}

	public void setProcess(boolean process) {
		this.process = process;
	}

	// public BlockPos getMachine() {
	// return machine;
	// }
	//
	// public void setMachine(BlockPos machine) {
	// this.machine = machine;
	// }

}
