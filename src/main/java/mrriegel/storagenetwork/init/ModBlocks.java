package mrriegel.storagenetwork.init;

import mrriegel.storagenetwork.blocks.BlockAnnexer;
import mrriegel.storagenetwork.blocks.BlockContainer;
import mrriegel.storagenetwork.blocks.BlockCover;
import mrriegel.storagenetwork.blocks.BlockCrafter;
import mrriegel.storagenetwork.blocks.BlockFKabel;
import mrriegel.storagenetwork.blocks.BlockFRequest;
import mrriegel.storagenetwork.blocks.BlockFannexer;
import mrriegel.storagenetwork.blocks.BlockFluidBox;
import mrriegel.storagenetwork.blocks.BlockIndicator;
import mrriegel.storagenetwork.blocks.BlockItemBox;
import mrriegel.storagenetwork.blocks.BlockKabel;
import mrriegel.storagenetwork.blocks.BlockMaster;
import mrriegel.storagenetwork.blocks.BlockRequest;
import mrriegel.storagenetwork.tile.TileAnnexer;
import mrriegel.storagenetwork.tile.TileContainer;
import mrriegel.storagenetwork.tile.TileCrafter;
import mrriegel.storagenetwork.tile.TileFRequest;
import mrriegel.storagenetwork.tile.TileFannexer;
import mrriegel.storagenetwork.tile.TileFluidBox;
import mrriegel.storagenetwork.tile.TileIndicator;
import mrriegel.storagenetwork.tile.TileItemBox;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {
	public static final Block master = new BlockMaster();
	public static final Block request = new BlockRequest();
	public static final Block frequest = new BlockFRequest();
	public static final Block kabel = new BlockKabel().setRegistryName("kabel");
	public static final Block storageKabel = new BlockKabel().setRegistryName("storageKabel");
	public static final Block exKabel = new BlockKabel().setRegistryName("exKabel");
	public static final Block imKabel = new BlockKabel().setRegistryName("imKabel");
	public static final Block vacuumKabel = new BlockKabel().setRegistryName("vacuumKabel");
	public static final Block fstorageKabel = new BlockFKabel().setRegistryName("fstorageKabel");
	public static final Block fexKabel = new BlockFKabel().setRegistryName("fexKabel");
	public static final Block fimKabel = new BlockFKabel().setRegistryName("fimKabel");
	public static final Block cover = new BlockCover();
	// public static final Block container = new BlockContainer();
	public static final Block crafter = new BlockCrafter();
	public static final Block indicator = new BlockIndicator();
	public static final Block annexer = new BlockAnnexer();
	public static final Block fannexer = new BlockFannexer();
	public static final Block itemBox = new BlockItemBox();
	public static final Block fluidBox = new BlockFluidBox();

	public static void init() {
		GameRegistry.register(master);
		GameRegistry.register(request);
		GameRegistry.register(frequest);
		GameRegistry.register(kabel.setUnlocalizedName(kabel.getRegistryName().toString()));
		GameRegistry.register(storageKabel.setUnlocalizedName(storageKabel.getRegistryName().toString()));
		GameRegistry.register(exKabel.setUnlocalizedName(exKabel.getRegistryName().toString()));
		GameRegistry.register(imKabel.setUnlocalizedName(imKabel.getRegistryName().toString()));
		GameRegistry.register(vacuumKabel.setUnlocalizedName(vacuumKabel.getRegistryName().toString()));
		GameRegistry.register(fstorageKabel.setUnlocalizedName(fstorageKabel.getRegistryName().toString()));
		GameRegistry.register(fexKabel.setUnlocalizedName(fexKabel.getRegistryName().toString()));
		GameRegistry.register(fimKabel.setUnlocalizedName(fimKabel.getRegistryName().toString()));
		GameRegistry.register(cover);
		GameRegistry.register(indicator);
		GameRegistry.register(annexer);
		GameRegistry.register(fannexer);
		GameRegistry.register(itemBox);
		GameRegistry.register(fluidBox);
		// GameRegistry.register(container);

		GameRegistry.register(new BlockMaster.Item(master).setRegistryName(master.getRegistryName()));
		GameRegistry.register(new BlockRequest.Item(request).setRegistryName(request.getRegistryName()));
		GameRegistry.register(new BlockFRequest.Item(frequest).setRegistryName(frequest.getRegistryName()));
		GameRegistry.register(new BlockKabel.Item(kabel).setRegistryName(kabel.getRegistryName()));
		GameRegistry.register(new BlockKabel.Item(storageKabel).setRegistryName(storageKabel.getRegistryName()));
		GameRegistry.register(new BlockKabel.Item(exKabel).setRegistryName(exKabel.getRegistryName()));
		GameRegistry.register(new BlockKabel.Item(imKabel).setRegistryName(imKabel.getRegistryName()));
		GameRegistry.register(new BlockKabel.Item(vacuumKabel).setRegistryName(vacuumKabel.getRegistryName()));
		GameRegistry.register(new BlockFKabel.Item(fstorageKabel).setRegistryName(fstorageKabel.getRegistryName()));
		GameRegistry.register(new BlockFKabel.Item(fexKabel).setRegistryName(fexKabel.getRegistryName()));
		GameRegistry.register(new BlockFKabel.Item(fimKabel).setRegistryName(fimKabel.getRegistryName()));
		GameRegistry.register(new BlockCover.Item(cover).setRegistryName(cover.getRegistryName()));
		GameRegistry.register(new BlockIndicator.Item(indicator).setRegistryName(indicator.getRegistryName()));
		GameRegistry.register(new BlockAnnexer.Item(annexer).setRegistryName(annexer.getRegistryName()));
		GameRegistry.register(new BlockFannexer.Item(fannexer).setRegistryName(fannexer.getRegistryName()));
		GameRegistry.register(new BlockItemBox.Item(itemBox).setRegistryName(itemBox.getRegistryName()));
		GameRegistry.register(new BlockFluidBox.Item(fluidBox).setRegistryName(fluidBox.getRegistryName()));
		// GameRegistry.register(new
		// BlockContainer.Item(container).setRegistryName(container.getRegistryName()));

		GameRegistry.registerTileEntity(TileKabel.class, "tileKabel");
		GameRegistry.registerTileEntity(TileMaster.class, "tileMaster");
		GameRegistry.registerTileEntity(TileRequest.class, "tileRequest");
		GameRegistry.registerTileEntity(TileFRequest.class, "tileFRequest");
		GameRegistry.registerTileEntity(TileContainer.class, "tileContainer");
		GameRegistry.registerTileEntity(TileCrafter.class, "tileCrafter");
		GameRegistry.registerTileEntity(TileIndicator.class, "tileIndicator");
		GameRegistry.registerTileEntity(TileAnnexer.class, "tileAnnexer");
		GameRegistry.registerTileEntity(TileFannexer.class, "tileFannexer");
		GameRegistry.registerTileEntity(TileItemBox.class, "tileItemBox");
		GameRegistry.registerTileEntity(TileFluidBox.class, "tileFluidBox");

	}

}
