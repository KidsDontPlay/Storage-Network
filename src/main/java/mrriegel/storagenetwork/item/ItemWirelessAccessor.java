package mrriegel.storagenetwork.item;

import java.util.List;

import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.item.CommonSubtypeItem;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.ModConfig;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.google.common.collect.Lists;

public class ItemWirelessAccessor extends CommonSubtypeItem {

	public ItemWirelessAccessor(String name) {
		super(name, 2);
		setCreativeTab(CreativeTab.TAB);
	}

	private boolean isFluid(ItemStack stack) {
		return stack.getItem().getRegistryName().toString().contains("fluid");
	}

	private boolean isAdvanced(ItemStack stack) {
		return stack.getItemDamage() == 1;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileNetworkCore && playerIn.isSneaking()) {
			NBTStackHelper.setLong(stack, "POS", pos.toLong());
			NBTStackHelper.setInt(stack, "dim", worldIn.provider.getDimension());
			System.out.println("komm");
			if (!worldIn.isRemote)
				playerIn.addChatComponentMessage(new TextComponentString("Network Core added."));
			return EnumActionResult.SUCCESS;
		}
		//		return EnumActionResult.SUCCESS;
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (hand == EnumHand.MAIN_HAND) {
			if (!itemStackIn.hasTagCompound() || !itemStackIn.getTagCompound().hasKey("sort")) {
				NBTStackHelper.setBoolean(itemStackIn, "top", true);
				NBTStackHelper.setBoolean(itemStackIn, "jei", false);
				NBTStackHelper.setInt(itemStackIn, "sort", Sort.NAME.ordinal());
				List<ItemStack> stacks = Lists.newArrayList();
				for (int i = 0; i < 9; i++)
					stacks.add(null);
				NBTStackHelper.setItemStackList(itemStackIn, "matrix", stacks);
			}
			if (!worldIn.isRemote && !playerIn.isSneaking()) {
				if (itemStackIn.getTagCompound().hasKey("POS")) {
					BlockPos corePos = BlockPos.fromLong(NBTStackHelper.getLong(itemStackIn, "POS"));
					int dim = NBTStackHelper.getInt(itemStackIn, "dim");
					World world = DimensionManager.getWorld(dim);
					if (!world.isBlockLoaded(corePos))
						playerIn.addChatComponentMessage(new TextComponentString("Network Core is unloaded."));
					if (world.isBlockLoaded(corePos) && world.getTileEntity(corePos) instanceof TileNetworkCore) {
						if (isAdvanced(itemStackIn) || Math.sqrt(corePos.distanceSq(new BlockPos(playerIn))) < ModConfig.rangeWirelessAccessor) {
							if (!isFluid(itemStackIn))
								playerIn.openGui(StorageNetwork.instance, GuiID.WIRELESS_ITEM.ordinal(), worldIn, 0, 0, 0);
							else
								//TODO fluid
								;
							return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
						} else
							playerIn.addChatComponentMessage(new TextComponentString("Network Core out of range."));
					} else
						playerIn.addChatComponentMessage(new TextComponentString("Network Core was removed."));
				} else {
					playerIn.addChatComponentMessage(new TextComponentString("No Network Core set."));
				}
			}
			if (!worldIn.isRemote && playerIn.isSneaking()) {
				itemStackIn.setTagCompound(null);
				playerIn.addChatComponentMessage(new TextComponentString("Network Core cleared."));
			}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

	public static TileNetworkCore getCore(ItemStack stack) {
		TileEntity e = DimensionManager.getWorld(NBTStackHelper.getInt(stack, "dim")).getTileEntity(BlockPos.fromLong(NBTStackHelper.getLong(stack, "POS")));
		if (e instanceof TileNetworkCore)
			return (TileNetworkCore) e;
		return null;
	}

}
