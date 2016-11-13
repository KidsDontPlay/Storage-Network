package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.FilterItem;
import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.container.ContainerRequestTable;
import mrriegel.storagenetwork.message.MessageItemListRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

public class TileRequestTable extends TileNetworkPart {

	public List<ItemStack> matrix = new ArrayList<ItemStack>(9);
	public Sort sort = Sort.NAME;
	public boolean topDown = true, jei = false;

	@Override
	public List<ItemStack> getDroppingItems() {
		return matrix;
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		if (getNetworkCore() != null) {
			if (worldObj.getBlockState(pos).getBlock() == Registry.requestTable)
				player.openGui(StorageNetwork.instance, GuiID.REQUEST_TABLE.ordinal(), worldObj, getX(), getY(), getZ());
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		matrix = NBTHelper.getItemStackList(compound.getCompoundTag("matrix"), "matrix");
		sort = Sort.values()[compound.getInteger("sort")];
		topDown = compound.getBoolean("top");
		jei = compound.getBoolean("jei");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound ss = new NBTTagCompound();
		NBTHelper.setItemStackList(ss, "matrix", matrix);
		compound.setTag("matrix", ss);
		compound.setInteger("sort", sort.ordinal());
		compound.setBoolean("top", topDown);
		compound.setBoolean("jei", jei);
		return super.writeToNBT(compound);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		switch (nbt.getInteger("button")) {
		case 0:
			sort = sort.next();
			break;
		case 1:
			topDown = !topDown;
			break;
		case 2:
			if (player.openContainer instanceof ContainerRequestTable && !player.worldObj.isRemote) {
				ContainerRequestTable con = (ContainerRequestTable) player.openContainer;
				for (int i = 0; i < 9; i++) {
					con.getMatrix().setInventorySlotContents(i, getNetworkCore().network.insertItem(con.getMatrix().getStackInSlot(i), getPosition(), false));
					matrix.set(i, con.getMatrix().getStackInSlot(i));
				}
				player.openContainer.detectAndSendChanges();
				break;
			}
		case 3:
			jei = !jei;
			break;
		case 1000:
			if (player.openContainer instanceof ContainerRequestTable) {
				TileNetworkCore core = ((ContainerRequestTable) player.openContainer).tile.getNetworkCore();
				if (core == null)
					return;
				ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
				if (stack != null) {
					int mouse = nbt.getInteger("mouse");
					int size = nbt.getBoolean("ctrl") ? 1 : mouse == 1 ? (nbt.getInteger("SIZE") < stack.getMaxStackSize() ? nbt.getInteger("SIZE") / 2 : stack.getMaxStackSize() / 2) : mouse == 0 ? stack.getMaxStackSize() : 0;
					ItemStack req = core.network.requestItem(new FilterItem(stack, true, false, true), size, false);
					if (req != null) {
						if (nbt.getBoolean("shift")) {
							player.inventory.addItemStackToInventory(req);
						} else {
							((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, 0, req));
							player.inventory.setItemStack(req);
						}

					}
				}
				player.openContainer.detectAndSendChanges();
				PacketHandler.sendTo(new MessageItemListRequest(core.network.getItemstacks()), (EntityPlayerMP) player);
			}
			break;
		case 1001:
			if (player.openContainer instanceof ContainerRequestTable) {
				TileNetworkCore core = ((ContainerRequestTable) player.openContainer).tile.getNetworkCore();
				if (core == null)
					return;
				ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
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
			}
			break;
		case 2000:
			NBTTagCompound n = new NBTTagCompound();
			n.setInteger("button", 2);
			handleMessage(player, n);
			boolean isempty = true;
			for (ItemStack s : matrix) {
				if (s != null) {
					isempty = false;
					break;
				}
			}
			if (isempty && getNetworkCore() != null && player.openContainer instanceof ContainerRequestTable) {
				for (int i = 0; i < 9; i++) {
					boolean ore = false;
					List<ItemStack> stacks = NBTHelper.getItemStackList(nbt, i + "l");
					if (stacks.isEmpty()) {
						stacks = OreDictionary.getOres(NBTHelper.getString(nbt, i + "s"));
						ore = true;
					}
					ItemStack stack = null;
					for (ItemStack s : stacks) {
						stack = getNetworkCore().network.requestItem(new FilterItem(s, true, ore, true), 1, false);
						if (stack != null)
							break;
					}
					if (stack != null) {
						((ContainerRequestTable) player.openContainer).inventorySlots.get(i + 1).putStack(stack);
						player.openContainer.detectAndSendChanges();
					}
				}
			}
			break;
		default:
			break;
		}
		if (!player.worldObj.isRemote)
			PacketHandler.sendTo(new MessageItemListRequest(getNetworkCore().network.getItemstacks()), (EntityPlayerMP) player);
	}
}
