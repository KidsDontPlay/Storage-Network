package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileCrafter;
import mrriegel.storagenetwork.tile.TileIndicator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLever;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIndicator extends BlockContainer {
	public static final PropertyBool STATE = PropertyBool.create("state");

	public BlockIndicator() {
		super(Material.iron);
		this.setHardness(1.1F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, false));
		this.setUnlocalizedName(StorageNetwork.MODID + ":indicator");
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return state.getValue(STATE) ? 15 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		System.out.println("new TE");
		return new TileIndicator();
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
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { STATE });
	}

	public void setState(World world, BlockPos pos, IBlockState state, boolean on) {
		TileEntity tileentity = world.getTileEntity(pos);
		world.setBlockState(pos, state.withProperty(STATE, on), 2);
		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(pos, tileentity);
		}
		world.markBlockForUpdate(pos);
		world.notifyNeighborsOfStateChange(pos, this);
//		world.markBlockRangeForRenderUpdate(pos.add(new Vec3i(1, 1, 1)), pos.subtract(new Vec3i(1, 1, 1)));

	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(STATE, false);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileIndicator) {
				playerIn.openGui(StorageNetwork.instance, GuiHandler.INDICATOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
	}
}
