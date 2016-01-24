package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockMaster extends BlockContainer {

	public BlockMaster() {
		super(Material.iron);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":master");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMaster();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		boolean hasMaster = false;
		for (BlockPos p : TileMaster.getSides(pos)) {
			if (worldIn.getTileEntity(p) instanceof TileKabel
					&& ((TileKabel) worldIn.getTileEntity(p)).getMaster() != null) {
				hasMaster = true;
				break;
			}
		}
		if (hasMaster) {
			worldIn.setBlockToAir(pos);
			Block.spawnAsEntity(worldIn, pos, Inv.copyStack(stack, 1));
		} else {
			if (worldIn.getTileEntity(pos) != null)
				((TileMaster) worldIn.getTileEntity(pos)).refreshNetwork();
		}
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		TileMaster tile = (TileMaster) worldIn.getTileEntity(pos);
		tile.refreshNetwork();
		if (!worldIn.isRemote && playerIn.getHeldItem() == null) {
			playerIn.addChatMessage(new ChatComponentText("Cables: "
					+ tile.cables.size()));
			int defaul = 0, storage = 0, ex = 0, im = 0, vac = 0;
			for (BlockPos p : tile.cables) {
				TileKabel k = (TileKabel) worldIn.getTileEntity(p);
				switch (k.getKind()) {
				case kabel:
					defaul++;
					break;
				case storageKabel:
					storage++;
					break;
				case exKabel:
					ex++;
					break;
				case imKabel:
					im++;
					break;
				case vacuumKabel:
					vac++;
					break;
				}
			}
			playerIn.addChatMessage(new ChatComponentText("   Link: " + defaul));
			playerIn.addChatMessage(new ChatComponentText("   Storage: "
					+ storage));
			playerIn.addChatMessage(new ChatComponentText("   Export: " + ex));
			playerIn.addChatMessage(new ChatComponentText("   Import: " + im));
			playerIn.addChatMessage(new ChatComponentText("   Vacuum: " + vac));
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side,
				hitX, hitY, hitZ);
	}

}
