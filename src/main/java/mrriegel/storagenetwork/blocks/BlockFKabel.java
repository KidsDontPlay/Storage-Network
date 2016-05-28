package mrriegel.storagenetwork.blocks;

import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

public class BlockFKabel extends BlockKabel {
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
		if (worldIn.isRemote)
			return true;
		if (/* tile.getMaster() == null || */(heldItem != null && (heldItem.getItem() == ModItems.coverstick || heldItem.getItem() == ModItems.toggler || heldItem.getItem() == ModItems.duplicator)))
			return false;
		else if (tile.getKind().isFluid()) {
			playerIn.openGui(StorageNetwork.instance, GuiHandler.FCABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;

		}
		playerIn.openContainer.detectAndSendChanges();
		return false;
	}

	boolean validInventory(World worldIn, BlockPos pos) {
		return worldIn.getTileEntity(pos) instanceof IFluidHandler;
	}

	boolean isConnectedToFluidHandler(IBlockAccess world, BlockPos orig, BlockPos pos) {
		IBlockState s = world.getBlockState(orig);
		for (BlockPos p : Util.getSides(orig)) {
			if (p.equals(pos))
				continue;
			if (world.getTileEntity(p) instanceof IFluidHandler && (((IFluidHandler) world.getTileEntity(p)).getTankInfo(get(orig, p)) != null) && (((IFluidHandler) world.getTileEntity(p)).getTankInfo(get(orig, p)).length != 0))
				return true;
		}
		return false;
	}

	@Override
	protected Connect getConnect(IBlockAccess worldIn, BlockPos orig, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		Block ori = worldIn.getBlockState(orig).getBlock();
		if (worldIn.getTileEntity(pos) instanceof IConnectable || worldIn.getTileEntity(pos) instanceof TileMaster)
			return Connect.CONNECT;
		if (ori == ModBlocks.kabel || ori == ModBlocks.vacuumKabel)
			return Connect.NULL;
		EnumFacing face = get(orig, pos);
		boolean sided = worldIn.getTileEntity(pos) instanceof IFluidHandler;
		if (!sided)
			return Connect.NULL;
		if (isConnectedToFluidHandler(worldIn, orig, pos))
			return Connect.NULL;
		return Connect.STORAGE;
	}

	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.fexKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.fkabel_E"));
			else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.fimKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.fkabel_I"));
			else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(ModBlocks.fstorageKabel))
				tooltip.add(I18n.format("tooltip.storagenetwork.fkabel_S"));
			tooltip.add(I18n.format("tooltip.storagenetwork.networkNeeded"));
		}

	}
}
