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
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.exKabel, 8),
				"kkk", "kpk", "kkk", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Blocks.piston));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.imKabel, 8),
				"kkk", "kpk", "kkk", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Blocks.hopper));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.storageKabel, 8),
				"kkk", "kpk", "kkk", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Blocks.chest));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.vacuumKabel, 8),
				"kkk", "kpk", "kkk", 'k', new ItemStack(ModBlocks.kabel), 'p',
				new ItemStack(Items.ender_pearl));
	}

}
