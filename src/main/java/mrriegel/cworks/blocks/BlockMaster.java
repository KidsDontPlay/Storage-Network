package mrriegel.cworks.blocks;

import mrriegel.cworks.CreativeTab;
import mrriegel.cworks.CableWorks;
import mrriegel.cworks.tile.TileMaster;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockMaster extends BlockContainer {

	public BlockMaster() {
		super(Material.iron);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(CableWorks.MODID + ":master");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMaster();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (worldIn.getTileEntity(pos) != null)
			((TileMaster) worldIn.getTileEntity(pos)).refreshNetwork();
	}

}
