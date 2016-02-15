package mrriegel.storagenetwork.init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
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
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.request), "dkd", "kck", "dkd", 'd', Items.gold_ingot, 'k', ModBlocks.kabel, 'c', Blocks.crafting_table);
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.master), "dkd", "kck", "dkd", 'd', Blocks.quartz_block, 'k', ModBlocks.kabel, 'c', Items.diamond);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 0), " c ", "gig", " c ", 'c', Blocks.redstone_block, 'i', Items.iron_ingot, 'g', Items.gold_ingot);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 1), "c", "i", "c", 'c', Items.comparator, 'i', Items.iron_ingot);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.upgrade, 1, 2), " c ", "gig", " c ", 'c', Blocks.redstone_block, 'i', Items.iron_ingot, 'g', Items.blaze_powder);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.remote, 1, 0), " c ", "eie", " c ", 'c', Items.gold_ingot, 'i', ModBlocks.storageKabel, 'e', Items.ender_pearl);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.remote, 1, 1), "c", "i", "d", 'c', Items.nether_star, 'i', ModItems.remote, 'd', Items.diamond);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.coverstick), " rg", " sb", "s  ", 's', "stickWood", 'r', "dyeRed", 'g', "dyeGreen", 'b', "dyeBlue"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.cover, 20), "dcd", "c c", "dcd", 'c', Blocks.hardened_clay, 'd', new ItemStack(Items.dye, 1, OreDictionary.WILDCARD_VALUE)) {
			@Override
			public ItemStack getCraftingResult(InventoryCrafting var1) {
				List<Integer> lis = new ArrayList<Integer>();
				for (int i = 0; i < var1.getSizeInventory(); i++) {
					if (var1.getStackInSlot(i) != null && var1.getStackInSlot(i).getItem() == Items.dye) {
						lis.add(var1.getStackInSlot(i).getItemDamage());
					}
				}
				Set<Integer> set = new HashSet<Integer>(lis);
				if (set.size() == 4)
					return output.copy();
				else
					return null;
			}
		});
	}

}
