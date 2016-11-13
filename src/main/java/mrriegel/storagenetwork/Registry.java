package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.item.CommonItem;
import mrriegel.storagenetwork.block.BlockItemAttractor;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;

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
	public static final CommonBlock networkEnergyInterface = new BlockNetworkEnergyInterface();
	public static final CommonBlock networkEnergyCell = new BlockNetworkEnergyCell();
	public static final CommonBlock networkToggleCable = new BlockNetworkToggleCable();
	public static final CommonBlock requestTable = new BlockRequestTable();
	public static final CommonBlock itemAttractor = new BlockItemAttractor();

	public static final CommonItem itemFilter = new ItemItemFilter();
	public static final CommonItem upgrade = new ItemUpgrade();

	public static void preInit() {
		networkCore.registerBlock();
		networkCable.registerBlock();
		networkExporter.registerBlock();
		networkImporter.registerBlock();
		networkStorage.registerBlock();
		networkEnergyInterface.registerBlock();
		networkEnergyCell.registerBlock();
		networkToggleCable.registerBlock();
		requestTable.registerBlock();
		itemAttractor.registerBlock();

		itemFilter.registerItem();
		upgrade.registerItem();

		initRecipes();
	}

	public static void preInitClient() {
		networkCore.initModel();
		networkCable.initModel();
		networkExporter.initModel();
		networkImporter.initModel();
		networkStorage.initModel();
		networkEnergyInterface.initModel();
		networkEnergyCell.initModel();
		networkToggleCable.initModel();
		requestTable.initModel();
		itemAttractor.initModel();

		itemFilter.initModel();
		upgrade.initModel();
		;

	}

	private static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(networkCable, 6), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);
		GameRegistry.addShapelessRecipe(new ItemStack(networkToggleCable), networkCable, Blocks.REDSTONE_TORCH);

		GameRegistry.addShapedRecipe(new ItemStack(itemFilter, 4), " i ", "isi", " i ", 'i', Blocks.IRON_BARS, 's', Items.STRING);
		ItemStack filter=new ItemStack(itemFilter, 2);
		NBTStackHelper.setBoolean(filter, "copy", true);
		GameRegistry.addRecipe(new ShapelessRecipes(filter, Lists.newArrayList(new ItemStack(itemFilter), new ItemStack(itemFilter))) {
			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv) {
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (s != null && s.hasTagCompound())
						return ItemHandlerHelper.copyStackWithSize(s, 2);

				}
				return super.getCraftingResult(inv);
			}
		});
	}

}
