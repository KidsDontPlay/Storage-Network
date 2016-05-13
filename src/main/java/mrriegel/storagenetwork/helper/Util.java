package mrriegel.storagenetwork.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;

public class Util {
	private static final Map<String, String> modNamesForIds = new HashMap<String, String>();
	private static List<ItemStack> stacks = Lists.newArrayList();

	public static void init() {
		Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
			String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
			String modName = modEntry.getValue().getName();
			modNamesForIds.put(lowercaseId, modName);
		}
		for (ResourceLocation r : Item.itemRegistry.getKeys()) {
			Item b = Item.itemRegistry.getObject(r);
			b.getSubItems(b, b.getCreativeTab(), stacks);
		}
	}

	@Nonnull
	public static String getModNameForItem(@Nonnull Item item) {
		ResourceLocation itemResourceLocation = GameData.getItemRegistry().getNameForObject(item);
		String modId = itemResourceLocation.getResourceDomain();
		String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
		String modName = modNamesForIds.get(lowercaseModId);
		if (modName == null) {
			modName = WordUtils.capitalize(modId);
			modNamesForIds.put(lowercaseModId, modName);
		}

		return modName;
	}

	public static String getModNameForFluid(Fluid fluid) {
		for (FluidContainerData d : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			if (fluid == d.fluid.getFluid())
				return getModNameForItem(d.filledContainer.getItem());
		}
		return "Unknown";
	}

	public static boolean equalOreDict(ItemStack a, ItemStack b) {
		int[] ar = OreDictionary.getOreIDs(a);
		int[] br = OreDictionary.getOreIDs(b);
		for (int i = 0; i < ar.length; i++)
			for (int j = 0; j < br.length; j++)
				if (ar[i] == br[j])
					return true;
		return false;
	}

	public static <E> boolean contains(List<E> list, E e, Comparator<? super E> c) {
		for (E a : list)
			if (c.compare(a, e) == 0)
				return true;
		return false;
	}

	public static FluidStack getFluid(ItemStack s) {
		if (s == null || s.getItem() == null)
			return null;
		FluidStack a = null;
		a = FluidContainerRegistry.getFluidForFilledItem(s);
		if (a != null)
			return a;
		if (s.getItem() instanceof IFluidContainerItem)
			a = ((IFluidContainerItem) s.getItem()).getFluid(s);
		return a;
	}

	public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
		if (stack == null || worldIn.isRemote)
			return;
		Random RANDOM = worldIn.rand;
		float f = RANDOM.nextFloat() * 0.8F + 0.1F;
		float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
		float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

		while (stack.stackSize > 0) {
			int i = RANDOM.nextInt(21) + 10;

			if (i > stack.stackSize) {
				i = stack.stackSize;
			}

			stack.stackSize -= i;
			EntityItem entityitem = new EntityItem(worldIn, x + f, y + f1, z + f2, new ItemStack(stack.getItem(), i, stack.getMetadata()));

			if (stack.hasTagCompound()) {
				entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
			}

			float f3 = 0.05F;
			entityitem.motionX = RANDOM.nextGaussian() * f3;
			entityitem.motionY = RANDOM.nextGaussian() * f3 + 0.20000000298023224D;
			entityitem.motionZ = RANDOM.nextGaussian() * f3;
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

	public static List<BlockPos> getSides(BlockPos pos) {
		List<BlockPos> lis = new ArrayList<BlockPos>();
		lis.add(pos.up());
		lis.add(pos.down());
		lis.add(pos.east());
		lis.add(pos.west());
		lis.add(pos.north());
		lis.add(pos.south());
		return lis;
	}

	public static ItemStack randomItem() {
		// int a = new Random().nextInt(Item.itemRegistry.getKeys().size());
		// return new
		// ItemStack(Item.itemRegistry.getObject(Lists.newArrayList(Item.itemRegistry.getKeys()).get(a)));
		int a = new Random().nextInt(stacks.size());
		return stacks.get(a);
	}
}
