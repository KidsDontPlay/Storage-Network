package mrriegel.storagenetwork.blocks;

import java.util.List;
import java.util.Random;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileFluidBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

public class BlockFluidBox extends BlockConnectable {

	public BlockFluidBox() {
		super(Material.IRON);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setRegistryName("fluidBox");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFluidBox();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (/* tile.getMaster() == null || */(heldItem != null && (heldItem.getItem() == ModItems.coverstick || heldItem.getItem() == ModItems.toggler || heldItem.getItem() == ModItems.duplicator)))
			return false;
		if (worldIn.getTileEntity(pos) instanceof TileFluidBox) {
			TileFluidBox tile = (TileFluidBox) worldIn.getTileEntity(pos);
			if (worldIn.isRemote)
				return true;
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
			tooltip.add(I18n.format("tooltip.storagenetwork.fluidbox"));
			tooltip.add(I18n.format("tooltip.storagenetwork.networkNeeded"));
			if (stack.getTagCompound() == null)
				return;
			FluidStack f = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
			if (f != null)
				tooltip.add("" + f.getLocalizedName() + ": " + (f.amount / 1000) + "B/" + ConfigHandler.fluidBoxCapacity + "B");
		}
	}

}
