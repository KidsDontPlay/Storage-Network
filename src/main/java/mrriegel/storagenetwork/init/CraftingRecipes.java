package mrriegel.storagenetwork.init;

import java.util.Arrays;
import java.util.List;

import mrriegel.storagenetwork.items.ItemRemote;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingRecipes {

	public static void init() {
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.kabel, 8), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.exKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Blocks.PISTON));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.imKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Blocks.HOPPER));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.storageKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Blocks.CHEST));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.vacuumKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Items.ENDER_PEARL));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fexKabel, 4), " k ", "kpk", " k ", 'p', new ItemStack(Items.BUCKET), 'k', new ItemStack(ModBlocks.exKabel));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fimKabel, 4), " k ", "kpk", " k ", 'p', new ItemStack(Items.BUCKET), 'k', new ItemStack(ModBlocks.imKabel));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fstorageKabel, 4), " k ", "kpk", " k ", 'p', new ItemStack(Items.BUCKET), 'k', new ItemStack(ModBlocks.storageKabel));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.request), "dkd", "kck", "dkd", 'd', Items.GOLD_INGOT, 'k', ModBlocks.kabel, 'c', Blocks.CRAFTING_TABLE);
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.frequest), new ItemStack(ModBlocks.request), new ItemStack(Items.BUCKET));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.master), "dkd", "kck", "dkd", 'd', Blocks.QUARTZ_BLOCK, 'k', ModBlocks.kabel, 'c', Items.DIAMOND);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 0), " c ", "gig", " c ", 'c', Blocks.REDSTONE_BLOCK, 'i', Items.IRON_INGOT, 'g', Items.GOLD_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 1), " c ", "rir", " c ", 'c', Items.COMPARATOR, 'i', Items.IRON_INGOT, 'r', Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 2), " c ", "gig", " c ", 'c', Blocks.REDSTONE_BLOCK, 'i', Items.IRON_INGOT, 'g', Items.BLAZE_POWDER);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 3), "c", "i", "c", 'c', Items.COMPARATOR, 'i', Items.IRON_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.remote, 1, 0), " c ", "eie", " c ", 'c', Items.GOLD_INGOT, 'i', ModBlocks.kabel, 'e', Items.ENDER_PEARL);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.remote, 1, 1), "c", "i", "d", 'c', Items.NETHER_STAR, 'i', ModItems.remote, 'd', Items.DIAMOND);
		class Foo extends ShapelessRecipes {
			public Foo(ItemStack output, List<ItemStack> inputList) {
				super(output, inputList);
			}

			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv) {
				ItemStack rem = null;
				ItemStack frem = super.getCraftingResult(inv);
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == ModItems.remote) {
						rem = inv.getStackInSlot(i);
						break;
					}
				}
				if (rem == null)
					return frem;
				else {
					if (rem.getTagCompound() != null)
						ItemRemote.copyTag(rem, frem);
					return frem;
				}
			}
		}
		Foo a = new Foo(new ItemStack(ModItems.fremote, 1, 0), Arrays.asList(new ItemStack(ModItems.remote, 1, 0), new ItemStack(Items.BUCKET)));
		GameRegistry.addRecipe(a);
		Foo b = new Foo(new ItemStack(ModItems.fremote, 1, 1), Arrays.asList(new ItemStack(ModItems.remote, 1, 1), new ItemStack(Items.BUCKET)));
		GameRegistry.addRecipe(b);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.coverstick), " rg", " sb", "s  ", 's', "stickWood", 'r', "dyeRed", 'g', "dyeGreen", 'b', "dyeBlue"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.cover, 24), "dcd", "c c", "dcd", 'c', Blocks.HARDENED_CLAY, 'd', new ItemStack(Items.DYE, 1, OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.indicator), " i ", "ioi", " i ", 'i', Items.IRON_INGOT, 'o', new ItemStack(ModItems.upgrade, 1, 1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.toggler), "  t", " s ", "i  ", 'i', Items.IRON_INGOT, 't', Blocks.REDSTONE_TORCH, 's', Items.STICK);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.annexer), "sps", "pcp", "s s", 's', "stone", 'p', new ItemStack(Items.IRON_PICKAXE), 'c', ModBlocks.kabel));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fannexer), "sps", "pcp", "s s", 's', "stone", 'p', new ItemStack(Items.BUCKET), 'c', ModBlocks.kabel));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.itemBox), "imi", "mgm", "imi", 'i', Items.IRON_INGOT, 'g', Items.GOLD_INGOT, 'm', Blocks.PLANKS));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fluidBox), "imi", "mgm", "imi", 'i', Items.IRON_INGOT, 'g', Items.GOLD_INGOT, 'm', Blocks.GLASS);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.duplicator), "  t", " s ", "i  ", 'i', Items.IRON_INGOT, 't', Items.PAPER, 's', Items.STICK);
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.duplicator), ModItems.duplicator);
	}

}
