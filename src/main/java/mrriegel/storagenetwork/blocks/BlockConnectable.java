package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockConnectable extends BlockContainer {

	public BlockConnectable(Material materialIn) {
		super(materialIn);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		boolean replaceable = false;
		try {
			replaceable = blockIn.isReplaceable(worldIn, pos.up());
		} catch (Exception e) {
			replaceable = true;
		}
		if (!blockIn.hasTileEntity() && blockIn != Blocks.AIR && !replaceable)
			return;
		for (BlockPos p : Util.getSides(pos)) {
			if (worldIn.getTileEntity(p) instanceof IConnectable) {
				if (((IConnectable) worldIn.getTileEntity(p)).getMaster() != null)
					((IConnectable) worldIn.getTileEntity(pos)).setMaster(((IConnectable) worldIn.getTileEntity(p)).getMaster());
			}
			if (worldIn.getTileEntity(p) instanceof TileMaster) {
				((IConnectable) worldIn.getTileEntity(pos)).setMaster(p);
			}
		}
		setConnections(worldIn, pos, state, true);
	}

	public void setConnections(World worldIn, BlockPos pos, IBlockState state, boolean refresh) {
		IConnectable tile = (IConnectable) worldIn.getTileEntity(pos);

		if (tile.getMaster() == null) {
			for (BlockPos p : Util.getSides(pos)) {
				if (worldIn.getTileEntity(p) instanceof TileMaster) {
					tile.setMaster(p);
					break;
				}
			}
		}
		if (tile.getMaster() != null) {
			TileEntity mas = worldIn.getTileEntity(tile.getMaster());
			tile.setMaster(null);
			((TileEntity) tile).markDirty();
			try {
				setAllMastersNull(worldIn, pos);
			} catch (Error e) {
				e.printStackTrace();
				if (mas instanceof TileMaster)
					for (BlockPos p : ((TileMaster) mas).connectables)
						if (worldIn.getChunkFromBlockCoords(p).isLoaded() && worldIn.getTileEntity(p) instanceof IConnectable) {
							((IConnectable) worldIn.getTileEntity(p)).setMaster(null);
							worldIn.getTileEntity(p).markDirty();
						}
			}
			if (refresh && mas instanceof TileMaster) {
				((TileMaster) mas).refreshNetwork();
			}
		}
		((TileEntity) tile).markDirty();
	}

	private void setAllMastersNull(World world, BlockPos pos) {
		((IConnectable) world.getTileEntity(pos)).setMaster(null);
		for (BlockPos bl : Util.getSides(pos)) {
			if (world.getChunkFromBlockCoords(bl).isLoaded() && world.getTileEntity(bl) instanceof IConnectable && ((IConnectable) world.getTileEntity(bl)).getMaster() != null) {
				((IConnectable) world.getTileEntity(bl)).setMaster(null);
				world.getTileEntity(bl).markDirty();
				setAllMastersNull(world, bl);
			}
		}
	}

}
