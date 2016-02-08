package mrriegel.storagenetwork.init;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.blocks.BlockKabel;
import mrriegel.storagenetwork.blocks.BlockMaster;
import mrriegel.storagenetwork.blocks.BlockRequest;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(value = StorageNetwork.MODID)
public class ModBlocks {
	public static final Block master = new BlockMaster();
	public static final Block request = new BlockRequest();
	public static final Block kabel = new BlockKabel().setUnlocalizedName(StorageNetwork.MODID + ":kabel");
	public static final Block storageKabel = new BlockKabel().setUnlocalizedName(StorageNetwork.MODID + ":storageKabel");
	public static final Block exKabel = new BlockKabel().setUnlocalizedName(StorageNetwork.MODID + ":exKabel");
	public static final Block imKabel = new BlockKabel().setUnlocalizedName(StorageNetwork.MODID + ":imKabel");
	public static final Block vacuumKabel = new BlockKabel().setUnlocalizedName(StorageNetwork.MODID + ":vacuumKabel");

	public static void init() {
		GameRegistry.registerBlock(master, "master");
		GameRegistry.registerBlock(request, "request");
		GameRegistry.registerBlock(kabel, "kabel");
		GameRegistry.registerBlock(storageKabel, "storageKabel");
		GameRegistry.registerBlock(exKabel, "exKabel");
		GameRegistry.registerBlock(imKabel, "imKabel");
		GameRegistry.registerBlock(vacuumKabel, "vacuumKabel");

		GameRegistry.registerTileEntity(TileKabel.class, "tileKabel");
		GameRegistry.registerTileEntity(TileMaster.class, "tileMaster");
		GameRegistry.registerTileEntity(TileRequest.class, "tileRequest");
	}

}
