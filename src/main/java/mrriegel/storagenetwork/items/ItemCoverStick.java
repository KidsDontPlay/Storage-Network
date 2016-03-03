package mrriegel.storagenetwork.items;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
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
			try {
				if (playerIn.inventory.currentItem >= 8)
					return false;
				boolean valid = true;
				if (playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1] == null)
					valid = false;
				Block b = !valid ? null : Block.getBlockFromItem(playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1].getItem());
				if (!playerIn.isSneaking() && b != null && (b.isBlockNormalCube() || b == Blocks.glass) && !(b instanceof ITileEntityProvider) && (playerIn.capabilities.isCreativeMode || tile.getCover() != null || playerIn.inventory.consumeInventoryItem(Item.getItemFromBlock(ModBlocks.cover)))) {
					tile.setCover(b);
					tile.setCoverMeta(playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1].getItemDamage());
					worldIn.markBlockForUpdate(pos);
					return true;
				} else if (!playerIn.isSneaking() && b == null && tile.getCover() != null) {
					tile.setCoverMeta(next(tile.getCoverMeta()));
					int count = 0;
					while (tile.getCover().getStateFromMeta(tile.getCoverMeta()).equals(tile.getCover().getStateFromMeta(next(tile.getCoverMeta())))) {
						tile.setCoverMeta(next(tile.getCoverMeta()));
						count++;
						if (count > 15) {
							tile.setCoverMeta(0);
							break;
						}
					}
					return true;
				} else if (playerIn.isSneaking()) {
					if (tile.getCover() != null) {
						tile.setCover(null);
						tile.setCoverMeta(0);
						if (!worldIn.isRemote && !playerIn.capabilities.isCreativeMode)
							worldIn.spawnEntityInWorld(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ModBlocks.cover)));
						worldIn.markBlockForUpdate(pos);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
	}

	private int next(int a) {
		if (a >= 15)
			return 0;
		else
			return a + 1;
	}
}
