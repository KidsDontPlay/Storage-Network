package mrriegel.storagenetwork.tile;

import java.util.List;

import mrriegel.storagenetwork.blocks.BlockAnnexer;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileAnnexer extends TileConnectable implements ITickable {

	@Override
	public void update() {
		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0 && master != null && worldObj.getTileEntity(master) instanceof TileMaster && !worldObj.isBlockPowered(pos)) {
			BlockPos p = pos.offset(worldObj.getBlockState(pos).getValue(BlockAnnexer.FACING).getOpposite());
			Block block = worldObj.getBlockState(p).getBlock();
			if (!canBreakBlock(block, p))
				return;
			List<ItemStack> lis = block.getDrops(worldObj, p, worldObj.getBlockState(p), 0);
			TileMaster mas = (TileMaster) worldObj.getTileEntity(master);
			if (!mas.consumeRF((int) (block.getBlockHardness(worldObj.getBlockState(p), worldObj, p) * 5f), false))
				return;
			worldObj.playEvent(2001, p, Block.getStateId(worldObj.getBlockState(p)));
			worldObj.setBlockToAir(p);
			if (worldObj.getTileEntity(p) != null)
				worldObj.removeTileEntity(p);
			block.dropXpOnBlockBreak(worldObj, p, block.getExpDrop(worldObj.getBlockState(p), worldObj, p, 0));
			for (ItemStack s : lis) {
				int rest = mas.insertStack(s, null, false);
				if (rest > 0) {
					ItemStack spawn = s.copy();
					spawn.stackSize = rest;
					spawnItemStack(worldObj, p, spawn);
				}
			}
		}
	}

	public void spawnItemStack(World worldIn, BlockPos pos, ItemStack stack) {
		if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) {
			float f = 0.5F;
			double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
			entityitem.setDefaultPickupDelay();
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

	private boolean canBreakBlock(Block block, BlockPos pos) {
		return !worldObj.isAirBlock(pos) && !block.getMaterial(worldObj.getBlockState(pos)).isLiquid() && block != Blocks.BEDROCK && block.getBlockHardness(worldObj.getBlockState(pos), worldObj, pos) > -1.0F && block.getHarvestLevel(worldObj.getBlockState(pos)) <= 3;
	}
}
