package mrriegel.storagenetwork.blocks;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.tile.TileItemBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class BlockDiskCase extends BlockConnectable {

	public BlockDiskCase() {
		super(Material.iron);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":diskCase");
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
			super.addInformation(stack, playerIn, tooltip, advanced);
			tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.diskCase"));
			tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.networkNeeded"));
			if (stack.getTagCompound() == null)
				return;
			tooltip.add("Slots: " + stack.getTagCompound().getTagList("box", Constants.NBT.TAG_COMPOUND).tagCount() + "/" + ConfigHandler.itemBoxCapacity);
		}

	}

}
