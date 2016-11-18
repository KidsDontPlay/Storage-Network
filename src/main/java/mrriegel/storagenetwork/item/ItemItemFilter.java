package mrriegel.storagenetwork.item;

import java.util.List;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.Utils;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import com.google.common.collect.Lists;

public class ItemItemFilter extends CommonItem {

	public ItemItemFilter() {
		super("item_item_filter");
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (hand == EnumHand.MAIN_HAND) {
			if (!itemStackIn.hasTagCompound() || !itemStackIn.getTagCompound().hasKey("white")) {
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
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (InvHelper.hasItemHandler(worldIn, pos, null)) {
			IItemHandler handler = InvHelper.getItemHandler(worldIn, pos, null);
			List<ItemStack> lis = Lists.newArrayList();
			for (int i = 0; i < handler.getSlots(); i++)
				if (handler.getStackInSlot(i) != null)
					lis.add(handler.getStackInSlot(i));
			while (lis.size() > 24)
				lis.remove(lis.size() - 1);
			while (lis.size() < 24)
				lis.add(null);
			NBTStackHelper.setItemStackList(stack, "inv", lis);
			if (!worldIn.isRemote)
				playerIn.addChatComponentMessage(new TextComponentString("Copied items to filter."));
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		if (NBTStackHelper.getBoolean(stack, "copy"))
			tooltip.add(TextFormatting.YELLOW + "Copy settings");
		if (!GuiScreen.isShiftKeyDown())
			tooltip.add(TextFormatting.ITALIC + "Press shift for more information");
		else {
			tooltip.add(TextFormatting.DARK_PURPLE + (NBTStackHelper.getBoolean(stack, "white") ? "Whitelist" : "Blacklist"));
			for (ItemStack s : getItems(stack))
				if (s != null)
					tooltip.add("  -" + s.getRarity().rarityColor + s.getDisplayName());
		}
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

	public static List<ItemStack> getItems(ItemStack filter) {
		return NBTStackHelper.getItemStackList(filter, "inv");
	}

	public static List<FilterItem> getFilterItems(ItemStack filter) {
		List<FilterItem> lis = Lists.newArrayList();
		for (ItemStack s : NBTStackHelper.getItemStackList(filter, "inv"))
			if (s != null)
				lis.add(new FilterItem(s, NBTStackHelper.getBoolean(filter, "meta"), NBTStackHelper.getBoolean(filter, "ore"), NBTStackHelper.getBoolean(filter, "nbt")));
		return lis;
	}

	public static boolean canTransferItem(ItemStack filter, ItemStack item) {
		if (item == null)
			return false;
		if (filter == null)
			return true;
		if (whiteList(filter))
			return match(filter, item);
		else
			return !match(filter, item);
	}

	public static boolean whiteList(ItemStack filter) {
		return NBTStackHelper.getBoolean(filter, "white");
	}

}
