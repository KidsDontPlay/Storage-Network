package mrriegel.storagenetwork.blocks;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.tile.TileContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockContainer extends BlockConnectable {

	public BlockContainer() {
		super(Material.IRON);
		this.setHardness(3.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setRegistryName("container");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileContainer();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		IConnectable tile = (IConnectable) worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && tile.getMaster() != null) {
			playerIn.openGui(StorageNetwork.instance, GuiHandler.CONTAINER, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileContainer) {
			TileContainer tile = (TileContainer) tileentity;
			for (int i = 0; i < 9; i++) {
				Util.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStackInSlot(i));
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			tooltip.add(I18n.format("tooltip.storagenetwork.container"));
			tooltip.add(I18n.format("tooltip.storagenetwork.networkNeeded"));
		}

	}
}
