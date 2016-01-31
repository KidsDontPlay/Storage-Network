package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.tile.TileMaster;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRemote extends Item {

	public ItemRemote() {
		super();
		this.setCreativeTab(CreativeTab.tab1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName(StorageNetwork.MODID + ":remote");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 2; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + "_" + stack.getItemDamage();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		if (NBTHelper.getBoolean(stack, "bound")) {
			tooltip.add(MinecraftServer.getServer().worldServerForDimension(
					NBTHelper.getInt(stack, "id")).provider.getDimensionName()
					+ " x: "
					+ (int) NBTHelper.getInt(stack, "x")
					+ ", y: "
					+ (int) NBTHelper.getInt(stack, "y")
					+ ", z: "
					+ (int) NBTHelper.getInt(stack, "z"));
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn,
			EntityPlayer playerIn) {
		int x = NBTHelper.getInt(itemStackIn, "x");
		int y = NBTHelper.getInt(itemStackIn, "y");
		int z = NBTHelper.getInt(itemStackIn, "z");
		World world = MinecraftServer.getServer().worldServerForDimension(
				NBTHelper.getInt(itemStackIn, "id"));
		if (NBTHelper.getBoolean(itemStackIn, "bound")) {
			if ((itemStackIn.getItemDamage() == 0
					&& NBTHelper.getInt(itemStackIn, "id") == worldIn.provider
							.getDimensionId() && playerIn.getDistance(x, y, z) <= 64)
					|| itemStackIn.getItemDamage() == 1)
				if (world.getChunkFromBlockCoords(new BlockPos(x, y, z))
						.isLoaded()) {
					((TileMaster) world.getTileEntity(new BlockPos(x, y, z)))
							.refreshNetwork();
					if (playerIn.getHeldItem().getTagCompound()
							.getString("sort").equals(""))
						NBTHelper.setString(playerIn.getHeldItem(), "sort",
								Sort.NAME.toString());
					playerIn.openGui(StorageNetwork.instance,
							GuiHandler.REMOTE, world, x, y, z);
				}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileMaster) {
			NBTHelper.setInteger(stack, "x", pos.getX());
			NBTHelper.setInteger(stack, "y", pos.getY());
			NBTHelper.setInteger(stack, "z", pos.getZ());
			NBTHelper.setBoolean(stack, "bound", true);
			NBTHelper
					.setInteger(stack, "id", worldIn.provider.getDimensionId());
			return true;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY,
				hitZ);
	}

	public static TileMaster getTile(ItemStack stack) {
		TileEntity t = MinecraftServer
				.getServer()
				.worldServerForDimension(NBTHelper.getInt(stack, "id"))
				.getTileEntity(
						new BlockPos(NBTHelper.getInt(stack, "x"), NBTHelper
								.getInt(stack, "y"), NBTHelper.getInt(stack,
								"z")));
		return t instanceof TileMaster ? (TileMaster) t : null;

	}
}
