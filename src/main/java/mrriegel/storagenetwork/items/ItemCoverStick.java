package mrriegel.storagenetwork.items;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemCoverStick extends Item {

	public ItemCoverStick() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":coverstick");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileKabel) {
			TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
			if (playerIn.inventory.currentItem >= 8)
				return false;
			ItemStack right = playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1];
			Block b = right == null ? null : Block.getBlockFromItem(right.getItem());
			if (!playerIn.isSneaking() && b != null && (b.isBlockNormalCube() || b == Blocks.glass) && !(b instanceof ITileEntityProvider) && (playerIn.capabilities.isCreativeMode || tile.getCover() != null || playerIn.inventory.consumeInventoryItem(Item.getItemFromBlock(ModBlocks.cover)))) {
				tile.setCover(b);
				tile.setCoverMeta(right.getItemDamage());
				worldIn.markBlockForUpdate(pos);
				playerIn.openContainer.detectAndSendChanges();
				return true;
			} else if (!playerIn.isSneaking() && b == null && tile.getCover() != null) {
				tile.setCoverMeta(nextMeta(tile.getCover(), tile.getCoverMeta()));
				worldIn.markBlockForUpdate(pos);
				playerIn.openContainer.detectAndSendChanges();
				return true;
			} else if (playerIn.isSneaking()) {
				if (tile.getCover() != null) {
					tile.setCover(null);
					tile.setCoverMeta(0);
					if (!worldIn.isRemote && !playerIn.capabilities.isCreativeMode)
						worldIn.spawnEntityInWorld(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ModBlocks.cover)));
					worldIn.markBlockForUpdate(pos);
					playerIn.openContainer.detectAndSendChanges();
					return true;
				}
			}
		}
		return false;
	}

	private int nextMeta(Block block, int meta) {
		if (!Item.getItemFromBlock(block).getHasSubtypes())
			return 0;
		if (meta >= 15)
			return 0;
		else {
			try {
				int res = meta + 1;
				while (block.getStateFromMeta(meta).equals(block.getStateFromMeta(res)))
					if (res < 15)
						res++;
					else
						res = 0;
				return res;
			} catch (ArrayIndexOutOfBoundsException e) {
				return 0;
			}
		}
	}

}
