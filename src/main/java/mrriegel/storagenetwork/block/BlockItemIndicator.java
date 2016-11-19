package mrriegel.storagenetwork.block;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileItemIndicator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockItemIndicator extends CommonBlockContainer<TileItemIndicator> {
	public static final PropertyBool STATE = PropertyBool.create("state");

	public BlockItemIndicator() {
		super(Material.IRON, "block_item_indicator");
		setHardness(2.5F);
		setCreativeTab(CreativeTab.TAB);
		setDefaultState(getDefaultState().withProperty(STATE, false));
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileItemIndicator();
	}

	@Override
	protected Class<? extends TileItemIndicator> getTile() {
		return TileItemIndicator.class;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return state.getValue(STATE) ? 15 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STATE, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STATE) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STATE });
	}

}
