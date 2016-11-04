package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.block.BlockNetworkCore;

/**
 * @author canitzp
 */
public class Registry {

    public static final CommonBlock networkCore = new BlockNetworkCore();
    public static final CommonBlock networkCable = new BlockNetworkCable();

    public static void preInit(){
        networkCore.registerBlock();
        networkCable.registerBlock();
    }
    
    public static void preInitClient(){
    	networkCore.initModel();
        networkCable.initModel();
    }

}
