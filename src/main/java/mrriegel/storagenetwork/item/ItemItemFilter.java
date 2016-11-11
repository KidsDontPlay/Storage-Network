package mrriegel.storagenetwork.item;

import java.util.List;

import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.Utils;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

public class ItemItemFilter extends CommonItem {

	public ItemItemFilter() {
		super("item_item_filter");
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (itemStackIn.getTagCompound() == null) {
			NBTStackHelper.setBoolean(itemStackIn, "meta", true);
			NBTStackHelper.setBoolean(itemStackIn, "nbt", false);
			NBTStackHelper.setBoolean(itemStackIn, "ore", false);
			NBTStackHelper.setBoolean(itemStackIn, "mod", false);
			NBTStackHelper.setBoolean(itemStackIn, "white", true);
			List<ItemStack> stacks = Lists.newArrayList();
			for (int i = 0; i < 24; i++)
				stacks.add(null);
			NBTStackHelper.setItemStackList(itemStackIn, "inv", stacks);
		}
		if (!worldIn.isRemote && !playerIn.isSneaking()) {
			playerIn.openGui(StorageNetwork.instance, GuiID.ITEM_FILTER.ordinal(), worldIn, 0, 0, 0);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		} else if (!worldIn.isRemote && playerIn.isSneaking()) {
			itemStackIn.setTagCompound(null);
			playerIn.addChatComponentMessage(new TextComponentString("Filter cleared."));
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

	public static boolean match(ItemStack filter, ItemStack item) {
		List<ItemStack> stacks = NBTStackHelper.getItemStackList(filter, "inv");
		boolean meta = NBTStackHelper.getBoolean(filter, "meta");
		boolean nbt = NBTStackHelper.getBoolean(filter, "nbt");
		boolean ore = NBTStackHelper.getBoolean(filter, "ore");
		boolean mod = NBTStackHelper.getBoolean(filter, "mod");
		for (ItemStack s : stacks) {
			if (s == null)
				continue;
			if (new FilterItem(s, meta, ore, nbt).match(item))
				return true;
			if (mod && Utils.getModID(item.getItem()).equals(Utils.getModID(s.getItem())))
				return true;
		}
		return false;
	}

	public static boolean canTransferItem(ItemStack filter, ItemStack item) {
		if (whiteList(filter))
			return match(filter, item);
		else
			return !match(filter, item);
	}

	public static boolean whiteList(ItemStack filter) {
		return NBTStackHelper.getBoolean(filter, "white");
	}

}
