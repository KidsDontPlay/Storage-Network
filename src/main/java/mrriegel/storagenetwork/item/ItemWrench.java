package mrriegel.storagenetwork.item;

import mrriegel.limelib.item.CommonItem;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.Enums.Connect;
import mrriegel.storagenetwork.block.BlockNetworkCable;
import mrriegel.storagenetwork.tile.TileNetworkCable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemWrench extends CommonItem {

	public ItemWrench() {
		super("item_wrench");
		setCreativeTab(CreativeTab.TAB);
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (!player.isSneaking()) {
			if (world.getBlockState(pos).getBlock().rotateBlock(world, pos, player.getHorizontalFacing().getOpposite()))
				if (!world.isRemote)
					return EnumActionResult.SUCCESS;
		} else if (!world.isRemote && world.getBlockState(pos).getBlock() instanceof BlockNetworkCable && player.isSneaking()) {
			IBlockState state = world.getBlockState(pos);
			EnumFacing f = ((BlockNetworkCable) world.getBlockState(pos).getBlock()).getFace(hitX, hitY, hitZ, state);
			TileNetworkCable tile = (TileNetworkCable) world.getTileEntity(pos);

			if (f != null) {
				tile.setSide(f, false);
				player.addChatMessage(new TextComponentString("Cable disconnected."));
				TileEntity newTile = world.getTileEntity(pos.offset(f));
				if (newTile != null && newTile instanceof TileNetworkCable) {
					((TileNetworkCable) newTile).setSide(f.getOpposite(), false);
					if (((TileNetworkCable) newTile).getNetworkCore() != null) {
						((TileNetworkCable) newTile).getNetworkCore().markForNetworkInit();
						BlockNetworkCable.releaseNetworkParts(world, newTile.getPos(), ((TileNetworkCable) newTile).getNetworkCore().getPos());
					}
				}
				if (tile.getNetworkCore() != null) {
					tile.getNetworkCore().markForNetworkInit();
					BlockNetworkCable.releaseNetworkParts(world, tile.getPos(), tile.getNetworkCore().getPos());
				}
				//				tile.markForSync();
			} else {
				if (state.getValue(BlockNetworkCable.bimap.get(side)) == Connect.NULL && !tile.isSideValid(side)) {
					tile.setSide(side, true);
					player.addChatMessage(new TextComponentString("Cable connected."));
					TileEntity newTile = world.getTileEntity(pos.offset(side));
					if (newTile != null && newTile instanceof TileNetworkCable) {
						((TileNetworkCable) newTile).setSide(side.getOpposite(), true);
						if (((TileNetworkCable) newTile).getNetworkCore() != null) {
							((TileNetworkCable) newTile).getNetworkCore().markForNetworkInit();
							BlockNetworkCable.releaseNetworkParts(world, newTile.getPos(), ((TileNetworkCable) newTile).getNetworkCore().getPos());
						}
					}
					if (tile.getNetworkCore() != null) {
						tile.getNetworkCore().markForNetworkInit();
						BlockNetworkCable.releaseNetworkParts(world, tile.getPos(), tile.getNetworkCore().getPos());
					}
					//					tile.markForSync();
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
	}

}
