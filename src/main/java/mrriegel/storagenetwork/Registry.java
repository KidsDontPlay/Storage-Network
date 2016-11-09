package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkCore;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author canitzp
 */
public class Registry {

    public static final CommonBlock networkCore = new BlockNetworkCore();
    public static final CommonBlock networkCable = new BlockNetworkCable();

    public static void preInit(){
        networkCore.registerBlock();
        networkCable.registerBlock();
        
        initRecipes();
    }
    
	public static void preInitClient(){
    	networkCore.initModel();
        networkCable.initModel();
    }
    
	private static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(networkCable, 8), "sss", "i i", "sss", 's', new ItemStack(Blocks.STONE_SLAB), 'i', Items.IRON_INGOT);		
	}
    

}
