package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LimitMessage implements IMessage, IMessageHandler<LimitMessage, IMessage> {
	int limit, x, y, z;
	ItemStack stack;

	public LimitMessage() {
	}

	public LimitMessage(int limit, int x, int y, int z, ItemStack stack) {
		super();
		this.limit = limit;
		this.x = x;
		this.y = y;
		this.z = z;
		this.stack = stack;
	}

	@Override
	public IMessage onMessage(final LimitMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileKabel tile = (TileKabel) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
				tile.setLimit(message.limit);
				tile.setStack(message.stack);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.limit = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.limit);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}

}
