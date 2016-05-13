package mrriegel.storagenetwork.blocks;

import java.util.List;
import java.util.Random;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.tile.TileFluidBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

public class BlockFluidBox extends BlockConnectable {

	public BlockFluidBox() {
		super(Material.iron);
		this.setHardness(3.0F);
		this.setCreativeTab(StorageNetwork.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":fluidbox");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFluidBox();
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileFluidBox tile = (TileFluidBox) worldIn.getTileEntity(pos);
		if (stack.getTagCompound() != null)
			tile.readTank(stack.getTagCompound());
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack stack = new ItemStack(getItemDropped(state, new Random(), fortune));
		NBTTagCompound x = new NBTTagCompound();
		((TileFluidBox) world.getTileEntity(pos)).writeTank(x);
		stack.setTagCompound(x);
		return Lists.newArrayList(stack);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileFluidBox) {
			TileFluidBox tile = (TileFluidBox) worldIn.getTileEntity(pos);
			if (worldIn.isRemote)
				return true;
			worldIn.markBlockForUpdate(pos);
			if (tile.getMaster() == null)
				return false;
			playerIn.openGui(StorageNetwork.instance, GuiHandler.FCABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;

	}

	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.fluidbox"));
			tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.networkNeeded"));
			if (stack.getTagCompound() == null)
				return;
			FluidStack f = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
			if (f != null)
				tooltip.add("" + f.getLocalizedName() + " " + (f.amount / 1000) + "B/" + ConfigHandler.fluidBoxCapacity + "B");
		}
	}

}
