package mrriegel.cworks.init;

import mrriegel.cworks.CableWorks;
import mrriegel.cworks.blocks.BlockKabel;
import mrriegel.cworks.blocks.BlockMaster;
import mrriegel.cworks.blocks.BlockRequest;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileMaster;
import mrriegel.cworks.tile.TileRequest;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(value = CableWorks.MODID)
public class ModBlocks {

	public static final Block kabel = new BlockKabel()
			.setUnlocalizedName(CableWorks.MODID + ":kabel");
	public static final Block storageKabel = new BlockKabel()
			.setUnlocalizedName(CableWorks.MODID + ":storageKabel");
	public static final Block exKabel = new BlockKabel()
			.setUnlocalizedName(CableWorks.MODID + ":exKabel");
	public static final Block imKabel = new BlockKabel()
			.setUnlocalizedName(CableWorks.MODID + ":imKabel");
	public static final Block vacuumKabel = new BlockKabel()
			.setUnlocalizedName(CableWorks.MODID + ":vacuumKabel");
	public static final Block master = new BlockMaster();
	public static final Block request = new BlockRequest();

	public static void init() {
		GameRegistry.registerBlock(kabel, "kabel");
		GameRegistry.registerBlock(storageKabel, "storageKabel");
		GameRegistry.registerBlock(exKabel, "exKabel");
		GameRegistry.registerBlock(imKabel, "imKabel");
		GameRegistry.registerBlock(vacuumKabel, "vacuumKabel");
		GameRegistry.registerBlock(master, "master");
		GameRegistry.registerBlock(request, "request");

		GameRegistry.registerTileEntity(TileKabel.class, "tileKabel");
		GameRegistry.registerTileEntity(TileMaster.class, "tileMaster");
		GameRegistry.registerTileEntity(TileRequest.class, "tileRequest");
	}

}
