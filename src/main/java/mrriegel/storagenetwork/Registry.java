package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.item.CommonItem;
import mrriegel.storagenetwork.block.BlockItemAttractor;
import mrriegel.storagenetwork.block.BlockItemBox;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkCore;
import mrriegel.storagenetwork.block.BlockNetworkEnergyCell;
import mrriegel.storagenetwork.block.BlockNetworkEnergyInterface;
import mrriegel.storagenetwork.block.BlockNetworkExporter;
import mrriegel.storagenetwork.block.BlockNetworkImporter;
import mrriegel.storagenetwork.block.BlockNetworkStorage;
import mrriegel.storagenetwork.block.BlockNetworkToggleCable;
import mrriegel.storagenetwork.block.BlockRequestTable;
import mrriegel.storagenetwork.item.ItemItemFilter;
import mrriegel.storagenetwork.item.ItemUpgrade;
import mrriegel.storagenetwork.item.ItemWirelessAccessor;
import mrriegel.storagenetwork.item.ItemWrench;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.google.common.collect.Lists;

/**
 * @author canitzp
 */
public class Registry {

	public static final CommonBlock networkCore = new BlockNetworkCore();
	public static final CommonBlock networkCable = new BlockNetworkCable("block_network_cable");
	public static final CommonBlock networkExporter = new BlockNetworkExporter();
	public static final CommonBlock networkImporter = new BlockNetworkImporter();
	public static final CommonBlock networkStorage = new BlockNetworkStorage();
	public static final CommonBlock networkToggleCable = new BlockNetworkToggleCable();
	public static final CommonBlock networkEnergyInterface = new BlockNetworkEnergyInterface();
	public static final CommonBlock networkEnergyCell = new BlockNetworkEnergyCell();
	public static final CommonBlock requestTable = new BlockRequestTable();
	public static final CommonBlock itemAttractor = new BlockItemAttractor();
	public static final CommonBlock itemBox = new BlockItemBox();

	public static final CommonItem wrench = new ItemWrench();
	public static final CommonItem itemFilter = new ItemItemFilter();
	public static final CommonItem upgrade = new ItemUpgrade();
	public static final CommonItem wireless = new ItemWirelessAccessor("item_wireless_accessor_item");

	public static void preInit() {
		networkCore.registerBlock();
		networkCable.registerBlock();
		networkExporter.registerBlock();
		networkImporter.registerBlock();
		networkStorage.registerBlock();
		networkToggleCable.registerBlock();
		networkEnergyInterface.registerBlock();
		networkEnergyCell.registerBlock();
		requestTable.registerBlock();
		itemAttractor.registerBlock();
		itemBox.registerBlock();

		wrench.registerItem();
		itemFilter.registerItem();
		upgrade.registerItem();
		wireless.registerItem();

		initRecipes();
	}

	public static void preInitClient() {
		networkCore.initModel();
		networkCable.initModel();
		networkExporter.initModel();
		networkImporter.initModel();
		networkStorage.initModel();
		networkToggleCable.initModel();
		networkEnergyInterface.initModel();
		networkEnergyCell.initModel();
		requestTable.initModel();
		itemAttractor.initModel();
		itemBox.initModel();

		wrench.initModel();
		itemFilter.initModel();
		upgrade.initModel();
		wireless.initModel();

	}

	private static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(networkCable, 6), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);
		GameRegistry.addShapelessRecipe(new ItemStack(networkToggleCable), networkCable, Blocks.REDSTONE_TORCH);
		GameRegistry.addShapedRecipe(new ItemStack(networkCore), "dkd", "kck", "dkd", 'd', Blocks.QUARTZ_BLOCK, 'k', networkCable, 'c', Items.DIAMOND);
		GameRegistry.addShapedRecipe(new ItemStack(networkExporter, 2), "ipi", "crc", 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 'c', networkCable, 'r', Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ItemStack(networkImporter, 2), "iri", "cpc", 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 'c', networkCable, 'r', Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ItemStack(networkStorage, 2), "iri", "cpc", 'i', Items.IRON_INGOT, 'p', Blocks.CHEST, 'c', networkCable, 'r', Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ItemStack(networkEnergyInterface, 2), "grg", "crc", 'g', Items.GOLD_NUGGET, 'c', networkCable, 'r', Items.REDSTONE);
		GameRegistry.addShapedRecipe(new ItemStack(networkEnergyCell), "cgc", "iri", "cgc", 'c', networkCable, 'i', Items.IRON_INGOT, 'r', Blocks.REDSTONE_BLOCK, 'g', Items.GOLD_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(requestTable), "cbc", "gwg", "ckc", 'c', networkCable, 'g', Items.GOLD_INGOT, 'b', Items.BUCKET, 'w', Blocks.CRAFTING_TABLE, 'k', Blocks.CHEST);
		GameRegistry.addShapedRecipe(new ItemStack(itemAttractor, 2), "iei", "chc", 'i', Items.IRON_INGOT, 'c', networkCable, 'h', Blocks.HOPPER, 'e', Items.ENDER_PEARL);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBox), "imi", "mgm", "imi", 'i', Items.IRON_INGOT, 'g', Items.GOLD_INGOT, 'm', "logWood"));

		GameRegistry.addShapedRecipe(new ItemStack(wrench), "i i", " c ", " i ", 'i', Items.IRON_INGOT, 'c', networkCable);
		GameRegistry.addShapedRecipe(new ItemStack(itemFilter, 4), " i ", "isi", " i ", 'i', Blocks.IRON_BARS, 's', Items.STRING);
		ShapelessRecipes r = new ShapelessRecipes(NBTStackHelper.setBoolean(new ItemStack(itemFilter, 2), "copy", true), Lists.newArrayList(new ItemStack(itemFilter), new ItemStack(itemFilter))) {
			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s != null && s.hasTagCompound())
						return ItemHandlerHelper.copyStackWithSize(s, 2);
				}
				return super.getCraftingResult(inv);
			}
		};
		RecipeSorter.register(StorageNetwork.MODID + ":itemFilterCopy", r.getClass(), Category.SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(r);
		GameRegistry.addShapedRecipe(new ItemStack(upgrade, 2, 0), " c ", "gig", " c ", 'c', Blocks.REDSTONE_BLOCK, 'i', Items.IRON_INGOT, 'g', Items.GOLD_INGOT);
		GameRegistry.addShapedRecipe(new ItemStack(upgrade, 2, 1), " c ", "gig", " c ", 'c', Blocks.REDSTONE_BLOCK, 'i', Items.IRON_INGOT, 'g', Items.BLAZE_POWDER);
		GameRegistry.addShapedRecipe(new ItemStack(wireless, 1, 0), " c ", "eie", " c ", 'c', Items.GOLD_NUGGET, 'i', requestTable, 'e', Items.ENDER_PEARL);
		GameRegistry.addShapedRecipe(new ItemStack(wireless, 1, 1), "c", "i", "d", 'c', Items.NETHER_STAR, 'i', wireless, 'd', Items.DIAMOND);
	}

}
