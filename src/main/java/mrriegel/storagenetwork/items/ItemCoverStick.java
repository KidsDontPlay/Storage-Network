package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.init.ModBlocks;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
				Block b = Block.getBlockFromItem(playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1].getItem());
				if ((b.isBlockNormalCube() || b == Blocks.glass) && !(b instanceof ITileEntityProvider) && (playerIn.capabilities.isCreativeMode || tile.getCover() != null || playerIn.inventory.consumeInventoryItem(Item.getItemFromBlock(ModBlocks.cover))) && !playerIn.isSneaking()) {
					tile.setCover(b);
					tile.setCoverMeta(playerIn.inventory.mainInventory[playerIn.inventory.currentItem + 1].getItemDamage());
					worldIn.markBlockForUpdate(pos);
					return true;
				} else if (playerIn.isSneaking()) {
					if (tile.getCover() != null) {
						tile.setCover(null);
						if (!worldIn.isRemote && !playerIn.capabilities.isCreativeMode)
							worldIn.spawnEntityInWorld(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, new ItemStack(ModBlocks.cover)));
						worldIn.markBlockForUpdate(pos);
						return true;
					}
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
	}
}
