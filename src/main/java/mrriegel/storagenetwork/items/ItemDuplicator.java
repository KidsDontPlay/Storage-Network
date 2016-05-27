package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.tile.AbstractFilterTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemDuplicator extends Item {

	public ItemDuplicator() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setRegistryName("duplicator");
		this.setUnlocalizedName(getRegistryName().toString());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof AbstractFilterTile) {
			AbstractFilterTile tile = (AbstractFilterTile) worldIn.getTileEntity(pos);
			boolean sneak = playerIn.isSneaking();
			if (sneak) {
				stack.setTagCompound(new NBTTagCompound());
				tile.writeSettings(stack.getTagCompound());
				NBTHelper.setBoolean(stack, "fluid", tile.isFluid());
				
				playerIn.addChatComponentMessage(new TextComponentString("Saved Data to " + getItemStackDisplayName(stack)));
			} else {
				if (stack.getTagCompound() != null)
					if ((NBTHelper.getBoolean(stack, "fluid") && tile.isFluid()) || (!NBTHelper.getBoolean(stack, "fluid") && !tile.isFluid())) {
						tile.readSettings(stack.getTagCompound());
						playerIn.addChatComponentMessage(new TextComponentString("Saved Data to " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
					}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("tooltip.storagenetwork.duplicator"));
	}

}
