package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.tile.AbstractFilterTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemDuplicator extends Item {

	public ItemDuplicator() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":duplicator");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof AbstractFilterTile) {
			AbstractFilterTile tile = (AbstractFilterTile) worldIn.getTileEntity(pos);
			boolean sneak = playerIn.isSneaking();
			if (sneak) {
				stack.setTagCompound(new NBTTagCompound());
				tile.writeSettings(stack.getTagCompound());
				NBTHelper.setBoolean(stack, "fluid", tile.isFluid());
				playerIn.addChatComponentMessage(new ChatComponentText("Saved Data to " + getItemStackDisplayName(stack)));
			} else {
				if (stack.getTagCompound() != null)
					if ((NBTHelper.getBoolean(stack, "fluid") && tile.isFluid()) || (!NBTHelper.getBoolean(stack, "fluid") && !tile.isFluid())) {
						tile.readSettings(stack.getTagCompound());
						playerIn.addChatComponentMessage(new ChatComponentText("Saved Data to " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
					}
			}
		}
		return false;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.duplicator"));
	}

}
