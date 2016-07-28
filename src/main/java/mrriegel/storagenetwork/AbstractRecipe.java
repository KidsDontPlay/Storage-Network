package mrriegel.storagenetwork;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import scala.actors.threadpool.Arrays;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

public class AbstractRecipe {
	protected List<ItemStack> output;
	protected boolean order;
	protected List<Object> input;

	public AbstractRecipe(List<ItemStack> output, boolean order, Object... input) {
		super();
		this.output = output;
		this.order = order;
		this.input = Lists.newArrayList(Arrays.asList(input));
	}

	public List<ItemStack> getOutput() {
		return output;
	}

	public boolean isOrder() {
		return order;
	}

	public List<Object> getInput() {
		return input;
	}

	public boolean match(List<ItemStack> list) {
		if (list.size() != input.size())
			return false;
		if (order) {
			for (int i = 0; i < input.size(); i++)
				if (!match(list.get(i), input.get(i)))
					return false;
			return true;
		} else {
			List<Object> foo = Lists.newArrayList(input);
			for (ItemStack stack : list) {
				if (stack != null) {
					boolean flag = false;
					for (int i = 0; i < foo.size(); i++) {
						Object o = foo.get(i);
						if (match(stack, o)) {
							flag = true;
							foo.remove(i);
							break;
						}
					}
					if (!flag) {
						return false;
					}
				}
			}
			return foo.isEmpty();
		}
	}

	public boolean match(ItemStack stack, Object o) {
		if (stack == null)
			return false;
		if (o instanceof Item || (o instanceof ItemStack) && ((ItemStack) o).getItemDamage() == OreDictionary.WILDCARD_VALUE)
			return stack.getItem() == o;
		if (o instanceof Block)
			return stack.getItem() == Item.getItemFromBlock((Block) o);
		if (o instanceof String)
			return Ints.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID((String) o));
		if (o instanceof ItemStack) {
			return stack.isItemEqual((ItemStack) o);
		}
		return false;
	}

}
