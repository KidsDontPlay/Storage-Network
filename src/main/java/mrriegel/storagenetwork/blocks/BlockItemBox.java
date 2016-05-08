package mrriegel.storagenetwork.blocks;

import java.util.List;
import java.util.Random;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.tile.TileItemBox;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

public class BlockItemBox extends BlockConnectable {

	public BlockItemBox() {
		super(Material.iron);
		this.setHardness(3.0F);
		this.setCreativeTab(StorageNetwork.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":itembox");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileItemBox();
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileItemBox tile = (TileItemBox) worldIn.getTileEntity(pos);
		if (stack.getTagCompound() != null)
			tile.readInventory(stack.getTagCompound());
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
		((TileItemBox) world.getTileEntity(pos)).writeInventory(x);
		stack.setTagCompound(x);
		return Lists.newArrayList(stack);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileItemBox) {
			TileItemBox tile = (TileItemBox) worldIn.getTileEntity(pos);
			if (worldIn.isRemote)
				return true;
			worldIn.markBlockForUpdate(pos);
			if (tile.getMaster() == null)
				return false;
			playerIn.openGui(StorageNetwork.instance, GuiHandler.CABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
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
			if (stack.getTagCompound() == null)
				return;
			tooltip.add("" + stack.getTagCompound().getTagList("box", Constants.NBT.TAG_COMPOUND).tagCount() + "/" + ConfigHandler.itemBoxCapacity);
		}

	}

}
