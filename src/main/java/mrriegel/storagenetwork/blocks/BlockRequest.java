package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockRequest extends BlockContainer {

	public BlockRequest() {
		super(Material.iron);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":request");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRequest();
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos,
			IBlockState state, Block neighborBlock) {
		for (BlockPos p : TileMaster.getSides(pos)) {
			if (worldIn.getTileEntity(p) instanceof TileKabel) {
				if (((TileKabel) worldIn.getTileEntity(p)).getMaster() != null)
					((TileRequest) worldIn.getTileEntity(pos))
							.setMaster(((TileKabel) worldIn.getTileEntity(p))
									.getMaster());
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		for (BlockPos p : TileMaster.getSides(pos)) {
			if (worldIn.getTileEntity(p) instanceof TileKabel) {
				if (((TileKabel) worldIn.getTileEntity(p)).getMaster() != null)
					((TileRequest) worldIn.getTileEntity(pos))
							.setMaster(((TileKabel) worldIn.getTileEntity(p))
									.getMaster());
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		TileRequest tile = (TileRequest) worldIn.getTileEntity(pos);
		worldIn.markBlockForUpdate(pos);
		if (tile.getMaster() != null) {
			playerIn.openGui(StorageNetwork.instance, GuiHandler.REQUEST, worldIn,
					pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side,
				hitX, hitY, hitZ);
	}

}
