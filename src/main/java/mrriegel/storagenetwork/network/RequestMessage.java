package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.FilterItem;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.google.common.collect.Lists;

public class RequestMessage implements IMessage, IMessageHandler<RequestMessage, IMessage> {
	int id, x, y, z;
	ItemStack stack;
	boolean shift, ctrl;

	public RequestMessage() {
	}

	public RequestMessage(int id, int x, int y, int z, ItemStack stack, boolean shift, boolean ctrl) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
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
					ItemStack stack = message.stack == null ? null : tile.request(message.stack, message.id == 0 ? message.stack.getMaxStackSize() : message.ctrl ? 1 : Math.max(message.stack.getMaxStackSize() / 2, 1), true, true, false, false);
					if (stack != null) {
						if (message.shift) {
							int rest = Inv.addToInventoryWithLeftover(stack, ctx.getServerHandler().playerEntity.inventory, false);
							if (rest != 0) {
								ctx.getServerHandler().playerEntity.dropPlayerItemWithRandomChoice(Inv.copyStack(stack, rest), false);
							}
						} else {
							ctx.getServerHandler().playerEntity.inventory.setItemStack(stack);
							PacketHandler.INSTANCE.sendTo(new StackMessage(stack), ctx.getServerHandler().playerEntity);
						}
					}
					FilterItem x = new FilterItem(new ItemStack(Blocks.bookshelf), true, false);
					List<FilterItem> missing = Lists.newArrayList();
					// System.out.println("can: " +
					// tile.getMissing(tile.getStacks(), x, 5, true,missing));
					// System.out.println(missing);
					PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REQUEST), ctx.getServerHandler().playerEntity);

				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
		this.shift = buf.readBoolean();
		this.ctrl = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeItemStack(buf, this.stack);
		buf.writeBoolean(this.shift);
		buf.writeBoolean(this.ctrl);
	}
}
