package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileFRequest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockFRequest extends BlockConnectable {

	public BlockFRequest() {
		super(Material.iron);
		this.setHardness(3.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":frequest");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFRequest();
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		IConnectable tile = (IConnectable) worldIn.getTileEntity(pos);
		if (tile.getMaster() != null) {
			playerIn.openGui(StorageNetwork.instance, GuiHandler.FREQUEST, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileFRequest) {
			TileFRequest tile = (TileFRequest) tileentity;
			Util.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.drain);
			Util.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.fill);
		}

		super.breakBlock(worldIn, pos, state);
	}

}
