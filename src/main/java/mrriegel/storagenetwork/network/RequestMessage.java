package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestMessage implements IMessage,
		IMessageHandler<RequestMessage, IMessage> {
	int id, x, y, z;
	ItemStack stack;

	public RequestMessage() {
	}

	public RequestMessage(int id, int x, int y, int z, ItemStack stack) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.stack = stack;
	}

	@Override
	public IMessage onMessage(final RequestMessage message,
			final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileMaster tile = (TileMaster) ctx.getServerHandler().playerEntity.worldObj
						.getTileEntity(new BlockPos(message.x, message.y,
								message.z));
				ItemStack stack = tile.request(message.stack,
						message.id == 0 ? 64 : 1, true, true);
				int rest = Inv.addToInventoryWithLeftover(stack,
						ctx.getServerHandler().playerEntity.inventory, false);
				if (rest != 0) {
					ctx.getServerHandler().playerEntity
							.dropPlayerItemWithRandomChoice(
									Inv.copyStack(stack, rest), false);
				}
				PacketHandler.INSTANCE.sendTo(
						new StacksMessage(tile.getStacks()),
						ctx.getServerHandler().playerEntity);

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
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}
}
