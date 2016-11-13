package mrriegel.storagenetwork.tile;

import java.util.ArrayList;
import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.FilterItem;
import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.container.ContainerRequestTable;
import mrriegel.storagenetwork.message.MessageItemListRequest;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.items.ItemHandlerHelper;

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
			if (worldObj.getBlockState(pos).getBlock() == Registry.blockRequestTable)
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
					int size = nbt.getBoolean("ctrl") ? 1 : mouse == 1 ? stack.getMaxStackSize() / 2 : mouse == 0 ? stack.getMaxStackSize() : 0;
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
		default:
			break;
		}
		if (!player.worldObj.isRemote)
			PacketHandler.sendTo(new MessageItemListRequest(getNetworkCore().network.getItemstacks()), (EntityPlayerMP) player);
	}

}
