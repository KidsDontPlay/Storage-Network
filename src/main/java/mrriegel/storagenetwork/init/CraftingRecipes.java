package mrriegel.storagenetwork.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingRecipes {

	public static void init() {
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.kabel, 16), "sss",
				"ili", "sss", 's', new ItemStack(Blocks.stone_slab), 'i',
				Items.iron_ingot, 'l', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.exKabel, 4),
				" k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Blocks.piston));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.imKabel, 4),
				" k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Blocks.hopper));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.storageKabel, 4),
				" k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Blocks.chest));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.vacuumKabel, 4),
				" k ", "kpk", " k ", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Items.ender_pearl));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.request), "dkd",
				"kck", "dkd", 'd', Items.gold_ingot, 'k', ModBlocks.kabel, 'c',
				Blocks.crafting_table);
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.master), "dkd",
				"kck", "dkd", 'd', Blocks.quartz_block, 'k', ModBlocks.kabel,
				'c', Items.diamond);
	}

}
