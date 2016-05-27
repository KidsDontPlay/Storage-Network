package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCoverStick extends Item {

	public ItemCoverStick() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setRegistryName("coverstick");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileKabel) {
			TileKabel tile = (TileKabel) worldIn.getTileEntity(pos);
			if (playerIn.inventory.currentItem >= 8)
				return EnumActionResult.PASS;
			ItemStack right = playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1];
			Block b = right == null ? null : Block.getBlockFromItem(right.getItem());
			if (!playerIn.isSneaking() && b != null && (b.isBlockNormalCube(b.getStateFromMeta(right.getItem().getDamage(right))) || b == Blocks.GLASS) && !(b instanceof ITileEntityProvider) && (playerIn.capabilities.isCreativeMode || tile.getCover() != null || 1 == playerIn.inventory.clearMatchingItems(Item.getItemFromBlock(ModBlocks.cover), 0, 1, null))) {
				tile.setCover(b);
				tile.setCoverMeta(right.getItemDamage());
				Util.updateTile(worldIn, pos);
				playerIn.openContainer.detectAndSendChanges();
				return EnumActionResult.SUCCESS;
			} else if (!playerIn.isSneaking() && b == null && tile.getCover() != null) {
				tile.setCoverMeta(nextMeta(tile.getCover(), tile.getCoverMeta()));
				Util.updateTile(worldIn, pos);
				playerIn.openContainer.detectAndSendChanges();
				return EnumActionResult.SUCCESS;
			} else if (playerIn.isSneaking()) {
				if (tile.getCover() != null) {
					tile.setCover(null);
					tile.setCoverMeta(0);
					if (!worldIn.isRemote && !playerIn.capabilities.isCreativeMode)
						worldIn.spawnEntityInWorld(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ModBlocks.cover)));
					Util.updateTile(worldIn, pos);
					playerIn.openContainer.detectAndSendChanges();
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	private int nextMeta(Block block, int meta) {
		if (meta >= 15)
			return 0;
		else {
			try {
				int res = meta + 1;
				int count = 0;
				while (block.getStateFromMeta(meta).equals(block.getStateFromMeta(res))) {
					if (res < 15)
						res++;
					else
						res = 0;
					count++;
					if (count > 15)
						return 0;
				}
				return res;
			} catch (ArrayIndexOutOfBoundsException e) {
				return 0;
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("tooltip.storagenetwork.coverstaff"));
	}

}
