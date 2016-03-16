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
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.kabel, 8), "sss", "ili", "sss", 's', new ItemStack(Blocks.stone_slab), 'i', Items.iron_ingot, 'l', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.exKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Blocks.piston));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.imKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Blocks.hopper));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.storageKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Blocks.chest));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.vacuumKabel, 4), " k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p', new ItemStack(Items.ender_pearl));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fexKabel, 4), " k ", "kpk", " k ", 'p', new ItemStack(Items.bucket), 'k', new ItemStack(ModBlocks.exKabel));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fimKabel, 4), " k ", "kpk", " k ", 'p', new ItemStack(Items.bucket), 'k', new ItemStack(ModBlocks.imKabel));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.fstorageKabel, 4), " k ", "kpk", " p ", 'p', new ItemStack(Items.bucket), 'k', new ItemStack(ModBlocks.storageKabel));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.request), "dkd", "kck", "dkd", 'd', Items.gold_ingot, 'k', ModBlocks.kabel, 'c', Blocks.crafting_table);
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.frequest), new ItemStack(ModBlocks.request), new ItemStack(Items.bucket));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.master), "dkd", "kck", "dkd", 'd', Blocks.quartz_block, 'k', ModBlocks.kabel, 'c', Items.diamond);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 0), " c ", "gig", " c ", 'c', Blocks.redstone_block, 'i', Items.iron_ingot, 'g', Items.gold_ingot);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 1), "c", "i", "c", 'c', Items.comparator, 'i', Items.iron_ingot);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 2), " c ", "gig", " c ", 'c', Blocks.redstone_block, 'i', Items.iron_ingot, 'g', Items.blaze_powder);
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.upgrade, 1, 3), new ItemStack(ModItems.upgrade, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.upgrade, 1, 1), new ItemStack(ModItems.upgrade, 1, 3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.remote, 1, 0), " c ", "eie", " c ", 'c', Items.gold_ingot, 'i', ModBlocks.storageKabel, 'e', Items.ender_pearl);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.remote, 1, 1), "c", "i", "d", 'c', Items.nether_star, 'i', ModItems.remote, 'd', Items.diamond);
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
		Foo a = new Foo(new ItemStack(ModItems.fremote, 1, 0), Arrays.asList(new ItemStack(ModItems.remote, 1, 0), new ItemStack(Items.bucket)));
		GameRegistry.addRecipe(a);
		Foo b = new Foo(new ItemStack(ModItems.fremote, 1, 1), Arrays.asList(new ItemStack(ModItems.remote, 1, 1), new ItemStack(Items.bucket)));
		GameRegistry.addRecipe(b);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.coverstick), " rg", " sb", "s  ", 's', "stickWood", 'r', "dyeRed", 'g', "dyeGreen", 'b', "dyeBlue"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.cover, 24), "dcd", "c c", "dcd", 'c', Blocks.hardened_clay, 'd', new ItemStack(Items.dye, 1, OreDictionary.WILDCARD_VALUE)));
	}

}
