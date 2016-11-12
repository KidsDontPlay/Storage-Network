package mrriegel.storagenetwork.block;

import static net.minecraft.block.BlockDirectional.FACING;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRequestTable extends CommonBlockContainer<TileRequestTable> {

	public BlockRequestTable(String name) {
		super(Material.IRON, name);
		setHardness(2.5F);
		setCreativeTab(CreativeTab.TAB);
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(pos, placer));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileRequestTable();
	}

	@Override
	protected Class<? extends TileRequestTable> getTile() {
		return TileRequestTable.class;
	}

}
