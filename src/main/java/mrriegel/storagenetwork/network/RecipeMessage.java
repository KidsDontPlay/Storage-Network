package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.gui.template.ContainerTemplate;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RecipeMessage implements IMessage, IMessageHandler<RecipeMessage, IMessage> {
	NBTTagCompound nbt;
	int index;

	public RecipeMessage() {
	}

	public RecipeMessage(NBTTagCompound nbt, int index) {
		this.nbt = nbt;
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.nbt = ByteBufUtils.readTag(buf);
		this.index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeInt(this.index);
	}

	@Override
	public IMessage onMessage(final RecipeMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (message.index == 0) {
					if (!(ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRequest))
						return;
					ContainerRequest con = (ContainerRequest) ctx.getServerHandler().playerEntity.openContainer;
					TileMaster tile = (TileMaster) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(con.tile.getMaster());
					for (int j = 1; j < 10; j++) {
						NBTTagList invList = message.nbt.getTagList("s" + j, Constants.NBT.TAG_COMPOUND);
						Map<Integer, ItemStack> lis = new HashMap<Integer, ItemStack>();
						for (int i = 0; i < invList.tagCount(); i++) {
							NBTTagCompound stackTag = invList.getCompoundTagAt(i);
							lis.put(i, ItemStack.loadItemStackFromNBT(stackTag));
						}
						for (int i = 0; i < lis.size(); i++) {
							ItemStack s = lis.get(i);
							if (s != null && con.craftMatrix.getStackInSlot(j - 1) == null && consumeItem(ctx.getServerHandler().playerEntity.inventory, s.copy())) {
								con.craftMatrix.setInventorySlotContents(j - 1, s);
								break;
							}
							s = tile.request(lis.get(i), 1, true, true, false, false);
							if (s != null && con.craftMatrix.getStackInSlot(j - 1) == null) {
								con.craftMatrix.setInventorySlotContents(j - 1, s);
								break;
							}
						}
					}
					con.slotChanged();
					PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REQUEST), ctx.getServerHandler().playerEntity);
				} else if (message.index == 1) {
					if (!(ctx.getServerHandler().playerEntity.openContainer instanceof ContainerTemplate))
						return;
					ContainerTemplate con = (ContainerTemplate) ctx.getServerHandler().playerEntity.openContainer;
					for (int j = 1; j < 10; j++) {
						con.craftMatrix.setInventorySlotContents(j - 1, null);
						NBTTagList invList = message.nbt.getTagList("s" + j, Constants.NBT.TAG_COMPOUND);
						Map<Integer, ItemStack> lis = new HashMap<Integer, ItemStack>();
						for (int i = 0; i < invList.tagCount(); i++) {
							NBTTagCompound stackTag = invList.getCompoundTagAt(i);
							lis.put(i, ItemStack.loadItemStackFromNBT(stackTag));
						}
						for (int i = 0; i < lis.size(); i++) {
							ItemStack s = lis.get(i);
							con.craftMatrix.setInventorySlotContents(j - 1, s);
							break;
						}
					}
					con.slotChanged(true);
					con.detectAndSendChanges();
				}

			}
		});
		return null;
	}

	boolean consumeItem(InventoryPlayer inv, ItemStack stack) {
		for (int i = 0; i < inv.getSizeInventory() - 4; i++) {
			if (inv.getStackInSlot(i) != null && inv.getStackInSlot(i).isItemEqual(stack)) {
				inv.decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}
}
