package mrriegel.storagenetwork.blocks;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileIndicator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIndicator extends BlockConnectable {
	public static final PropertyBool STATE = PropertyBool.create("state");

	public BlockIndicator() {
		super(Material.IRON);
		this.setHardness(3.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, false));
		this.setRegistryName("indicator");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return state.getValue(STATE) ? 15 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STATE });
	}

	public void setState(World world, BlockPos pos, IBlockState state, boolean on) {
		TileEntity tileentity = world.getTileEntity(pos);
		world.setBlockState(pos, state.withProperty(STATE, on), 2);
		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(pos, tileentity);
		}
		Util.updateTile(world, pos);
		world.notifyNeighborsOfStateChange(pos, this);

	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(STATE, false);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileIndicator && ((TileIndicator) tileentity).getMaster() != null) {
				playerIn.openGui(StorageNetwork.instance, GuiHandler.INDICATOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
	}

	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			tooltip.add(I18n.format("tooltip.storagenetwork.indicator"));
			tooltip.add(I18n.format("tooltip.storagenetwork.networkNeeded"));
		}

	}
}
