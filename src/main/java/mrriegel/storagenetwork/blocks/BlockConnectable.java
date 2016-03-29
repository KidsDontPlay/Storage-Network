package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class BlockConnectable extends BlockContainer {

	public BlockConnectable(Material materialIn) {
		super(materialIn);
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
		onNeighborBlockChange(worldIn, pos, state, Blocks.air);

	}

	private void setConnections(World worldIn, BlockPos pos) {
		IConnectable tile = (IConnectable) worldIn.getTileEntity(pos);

		if (tile.getMaster() != null) {
			TileEntity mas = worldIn.getTileEntity(tile.getMaster());
			tile.setMaster(null);
			removeAllMasters(worldIn, pos);
			if (mas instanceof TileMaster) {
				((TileMaster) mas).refreshNetwork(true);
			}
		}
	}

	public void removeAllMasters(World world, BlockPos pos) {
		((IConnectable) world.getTileEntity(pos)).setMaster(null);
		for (BlockPos bl : Util.getSides(pos)) {
			if (world.getTileEntity(bl) instanceof IConnectable && world.getChunkFromBlockCoords(bl).isLoaded() && ((IConnectable) world.getTileEntity(bl)).getMaster() != null) {
				((TileMaster) world.getTileEntity(((IConnectable) world.getTileEntity(bl)).getMaster())).removeIConnectable(bl);
				((IConnectable) world.getTileEntity(bl)).setMaster(null);
				removeAllMasters(world, bl);
			}
		}
	}
}
