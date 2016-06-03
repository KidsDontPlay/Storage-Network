package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.remote.ContainerRemote;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestMessage implements IMessage, IMessageHandler<RequestMessage, IMessage> {
	int id;
	ItemStack stack;
	boolean shift, ctrl;

	public RequestMessage() {
	}

	public RequestMessage(int id, ItemStack stack, boolean shift, boolean ctrl) {
		this.id = id;
		this.stack = stack;
		this.shift = shift;
		this.ctrl = ctrl;
	}

	@Override
	public IMessage onMessage(final RequestMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRequest) {
					TileMaster tile = (TileMaster) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(((ContainerRequest) ctx.getServerHandler().playerEntity.openContainer).tile.getMaster());
					if (tile == null)
						return;
					ItemStack stack = message.stack == null ? null : tile.request(new FilterItem(message.stack, true, false, true), message.id == 0 ? message.stack.getMaxStackSize() : message.ctrl ? 1 : Math.max(message.stack.getMaxStackSize() / 2, 1), false);
					if (stack != null) {
						if (message.shift) {
							int rest = Inv.addToInventoryWithLeftover(stack, ctx.getServerHandler().playerEntity.inventory, false);
							if (rest != 0) {
								ctx.getServerHandler().playerEntity.dropItem(Inv.copyStack(stack, rest), false);
							}
						} else {
							ctx.getServerHandler().playerEntity.inventory.setItemStack(stack);
							PacketHandler.INSTANCE.sendTo(new StackMessage(stack), ctx.getServerHandler().playerEntity);
						}
					}

					PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REQUEST), ctx.getServerHandler().playerEntity);

				} else if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRemote) {
					TileMaster tile = ItemRemote.getTile(ctx.getServerHandler().playerEntity.inventory.getCurrentItem());
					if (tile == null)
						return;
					ItemStack stack = message.stack == null ? null : tile.request(new FilterItem(message.stack, true, false, true), message.id == 0 ? message.stack.getMaxStackSize() : message.ctrl ? 1 : Math.max(message.stack.getMaxStackSize() / 2, 1), false);
					if (stack != null) {
						if (message.shift) {
							int rest = Inv.addToInventoryWithLeftover(stack, ctx.getServerHandler().playerEntity.inventory, false);
							if (rest != 0) {
								ctx.getServerHandler().playerEntity.dropItem(Inv.copyStack(stack, rest), false);
							}
						} else {
							ctx.getServerHandler().playerEntity.inventory.setItemStack(stack);
							PacketHandler.INSTANCE.sendTo(new StackMessage(stack), ctx.getServerHandler().playerEntity);
						}
					}
					PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REMOTE), ctx.getServerHandler().playerEntity);
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
		this.shift = buf.readBoolean();
		this.ctrl = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		ByteBufUtils.writeItemStack(buf, this.stack);
		buf.writeBoolean(this.shift);
		buf.writeBoolean(this.ctrl);
	}
}
