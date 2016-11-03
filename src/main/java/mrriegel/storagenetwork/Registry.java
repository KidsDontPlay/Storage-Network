package mrriegel.storagenetwork;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.storagenetwork.block.BlockNetworkCore;

/**
 * @author canitzp
 */
public class Registry {

    public static CommonBlock networkCore = new BlockNetworkCore();

    public static void preInit(){
        networkCore.registerBlock();
    }

}
