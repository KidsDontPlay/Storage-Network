package mrriegel.storagenetwork.blocks;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileToggler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockToggle extends BlockConnectable {

	public BlockToggle() {
		super(Material.IRON);
		this.setHardness(3.0F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setRegistryName("toggler");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (worldIn.getTileEntity(pos) instanceof TileToggler) {
			TileToggler tile = (TileToggler) worldIn.getTileEntity(pos);
			boolean x = worldIn.isBlockPowered(pos);
			boolean changed = x != tile.isDisabled();
			if (changed) {
				tile.setDisabled(x);
				tile.markDirty();
				BlockPos master = tile.getMaster();
				if (master != null && worldIn.getChunkFromBlockCoords(master).isLoaded() && worldIn.getTileEntity(master) instanceof TileMaster) {
					((TileMaster) worldIn.getTileEntity(master)).refreshNetwork();
				}
			}
		}
		super.neighborChanged(state, worldIn, pos, blockIn);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileToggler();
	}

	public static class Item extends ItemBlock {

		public Item(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			super.addInformation(stack, playerIn, tooltip, advanced);
			tooltip.add(I18n.format("tooltip.storagenetwork.toggler"));
			tooltip.add(I18n.format("tooltip.storagenetwork.networkNeeded"));
		}

	}
}
