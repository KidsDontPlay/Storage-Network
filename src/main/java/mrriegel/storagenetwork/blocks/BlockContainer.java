package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileContainer;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockContainer extends net.minecraft.block.BlockContainer {

	public BlockContainer() {
		super(Material.iron);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":container");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileContainer();
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		for (BlockPos p : Util.getSides(pos)) {
			if (worldIn.getTileEntity(p) instanceof IConnectable) {
				if (((IConnectable) worldIn.getTileEntity(p)).getMaster() != null)
					((IConnectable) worldIn.getTileEntity(pos)).setMaster(((IConnectable) worldIn.getTileEntity(p)).getMaster());
			}
			if (worldIn.getTileEntity(p) instanceof TileMaster) {
				((IConnectable) worldIn.getTileEntity(pos)).setMaster(p);
			}
		}
		setConnections(worldIn, pos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		onNeighborBlockChange(worldIn, pos, state, null);
	}

	public static void setConnections(World worldIn, BlockPos pos) {
		IConnectable tile = (IConnectable) worldIn.getTileEntity(pos);
		if (tile.getMaster() == null) {
			for (BlockPos p : Util.getSides(pos)) {
				if (worldIn.getTileEntity(p) instanceof TileMaster) {
					tile.setMaster(p);
				}
			}
		}
		if (tile.getMaster() != null) {
			TileEntity mas = worldIn.getTileEntity(tile.getMaster());

			tile.setMaster(null);
			worldIn.markBlockForUpdate(pos);
			BlockKabel.setAllMastersNull(worldIn, pos);
			if (mas instanceof TileMaster) {
				((TileMaster) mas).refreshNetwork();
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		IConnectable tile = (IConnectable) worldIn.getTileEntity(pos);
		worldIn.markBlockForUpdate(pos);
		if (/* !worldIn.isRemote && */tile.getMaster() != null) {
			((TileMaster) worldIn.getTileEntity(tile.getMaster())).refreshNetwork();
			worldIn.markBlockForUpdate(pos);
			playerIn.openGui(StorageNetwork.instance, GuiHandler.CONTAINER, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileContainer) {
			TileContainer tile = (TileContainer) tileentity;
			for (int i = 0; i < 9; i++) {
				Util.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStackInSlot(i));
			}
		}

		super.breakBlock(worldIn, pos, state);
	}
}
