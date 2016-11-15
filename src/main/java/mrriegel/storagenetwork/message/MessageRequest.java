package mrriegel.storagenetwork.message;

import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.network.AbstractMessage;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.FilterItem;
import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.container.ContainerAbstractRequest;
import mrriegel.storagenetwork.item.ItemWirelessAccessor;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

public class MessageRequest extends AbstractMessage<MessageRequest> {

	public MessageRequest() {
	}

	public MessageRequest(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		if (player.openContainer instanceof ContainerAbstractRequest) {
			ContainerAbstractRequest<?> con = (ContainerAbstractRequest<?>) player.openContainer;
			TileRequestTable tile = null;
			ItemStack wireless = null;
			if (con.getObject() instanceof TileRequestTable)
				tile = (TileRequestTable) con.getObject();
			else if (con.getObject() instanceof ItemStack)
				wireless = (ItemStack) con.getObject();
			else
				return;
			TileNetworkCore core = tile != null ? tile.getNetworkCore() : ItemWirelessAccessor.getCore(wireless);
			if (core == null)
				return;
			ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
			switch (nbt.getInteger("button")) {
			case 0:
				if (tile != null)
					tile.sort = tile.sort.next();
				else
					NBTStackHelper.setInt(wireless, "sort", Sort.values()[NBTStackHelper.getInt(wireless, "sort")].next().ordinal());
				break;
			case 1:
				if (tile != null)
					tile.topDown = !tile.topDown;
				else
					NBTStackHelper.setBoolean(wireless, "top", !NBTStackHelper.getBoolean(wireless, "top"));
				break;
			case 2:
				if (!player.worldObj.isRemote) {
					List<ItemStack> lis = Lists.newArrayList();
					for (int i = 0; i < 9; i++) {
						con.getMatrix().setInventorySlotContents(i, core.network.insertItem(con.getMatrix().getStackInSlot(i), null, false));
						lis.add(con.getMatrix().getStackInSlot(i));
					}
					if (tile != null)
						tile.matrix = lis;
					else
						NBTStackHelper.setItemStackList(wireless, "matrix", lis);
					player.openContainer.detectAndSendChanges();
					break;
				}
			case 3:
				if (tile != null)
					tile.jei = !tile.jei;
				else
					NBTStackHelper.setBoolean(wireless, "jei", !NBTStackHelper.getBoolean(wireless, "jei"));
				break;
			case 1000:
				if (stack != null) {
					int mouse = nbt.getInteger("mouse");
					int size = nbt.getBoolean("ctrl") ? 1 : mouse == 1 ? (nbt.getInteger("SIZE") < stack.getMaxStackSize() ? Math.max(nbt.getInteger("SIZE") / 2, 1) : stack.getMaxStackSize() / 2) : mouse == 0 ? stack.getMaxStackSize() : 0;
					ItemStack req = core.network.requestItem(new FilterItem(stack, true, false, true), size, false);
					if (req != null) {
						if (nbt.getBoolean("shift")) {
							player.dropItem(ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(player.inventory), req, false), false);
						} else {
							((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, 0, req));
							player.inventory.setItemStack(req);
						}

					}
				}
				player.openContainer.detectAndSendChanges();
				PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) player);
				break;
			case 1001:
				int mouse = nbt.getInteger("mouse");
				if (mouse == 0 || mouse == 1) {
					ItemStack rest = null;
					if (mouse == 0)
						rest = core.network.insertItem(stack, null, false);
					else {
						ItemStack x = core.network.insertItem(ItemHandlerHelper.copyStackWithSize(stack, 1), null, false);
						if (x == null)
							rest = ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - 1);
						else
							rest = stack;
					}
					((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, 0, rest));
					player.inventory.setItemStack(rest);
					player.openContainer.detectAndSendChanges();
					PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) player);
				}
				break;
			case 2000:
				NBTTagCompound n = new NBTTagCompound();
				n.setInteger("button", 2);
				handleMessage(player, n, side);
				boolean isempty = true;
				for (ItemStack s : con.getMatrixList()) {
					if (s != null) {
						isempty = false;
						break;
					}
				}
				if (isempty) {
					for (int i = 0; i < 9; i++) {
						boolean ore = false;
						List<ItemStack> stacks = NBTHelper.getItemStackList(nbt, i + "l");
						if (stacks.isEmpty()) {
							stacks = OreDictionary.getOres(NBTHelper.getString(nbt, i + "s"));
							ore = true;
						}
						ItemStack ingredient = null;
						for (ItemStack s : stacks) {
							ingredient = core.network.requestItem(new FilterItem(s, true, ore, true), 1, false);
							if (ingredient != null)
								break;
						}
						if (ingredient != null) {
							con.inventorySlots.get(i + 1).putStack(ingredient);
							player.openContainer.detectAndSendChanges();
						}
					}
				}
				break;
			default:
				break;
			}
			if (!player.worldObj.isRemote && core != null)
				PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) player);
		}
	}

}
