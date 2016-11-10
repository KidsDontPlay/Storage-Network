package mrriegel.storagenetwork.block;

import mrriegel.storagenetwork.tile.TileNetworkConnection;
import mrriegel.storagenetwork.tile.TileNetworkTunnel;
import mrriegel.storagenetwork.tile.TileNetworkTunnel.Mode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNetworkTunnel extends BlockNetworkConnection {

	public BlockNetworkTunnel(String name) {
		super(name);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		((TileNetworkTunnel) worldIn.getTileEntity(pos)).mode = getMode();
	}

	public Mode getMode() {
		if (getRegistryName().toString().contains("energy"))
			return Mode.ENERGY;
		if (getRegistryName().toString().contains("item"))
			return Mode.ITEM;
		if (getRegistryName().toString().contains("fluid"))
			return Mode.FLUID;
		if (getRegistryName().toString().contains("redstone"))
			return Mode.REDSTONE;
		return null;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		//		if(getRegistryName().toString().contains("energy"))
		//			return Mode.ENERGY;
		//		if(getRegistryName().toString().contains("item"))
		//			return Mode.ITEM;
		//		if(getRegistryName().toString().contains("fluid"))
		//			return Mode.FLUID;
		//		if(getRegistryName().toString().contains("redstone"))
		//			return Mode.REDSTONE;
		return new TileNetworkTunnel();
	}

	@Override
	protected Class<? extends TileNetworkConnection> getTile() {
		//		if(getRegistryName().toString().contains("energy"))
		//			return Mode.ENERGY;
		//		if(getRegistryName().toString().contains("item"))
		//			return Mode.ITEM;
		//		if(getRegistryName().toString().contains("fluid"))
		//			return Mode.FLUID;
		//		if(getRegistryName().toString().contains("redstone"))
		//			return Mode.REDSTONE;
		return TileNetworkTunnel.class;
	}

}
