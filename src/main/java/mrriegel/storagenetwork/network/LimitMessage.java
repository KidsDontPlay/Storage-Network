package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LimitMessage implements IMessage, IMessageHandler<LimitMessage, IMessage> {
	int limit;
	BlockPos pos;
	ItemStack stack;

	public LimitMessage() {
	}

	public LimitMessage(int limit, BlockPos pos, ItemStack stack) {
		super();
		this.limit = limit;
		this.pos = pos;
		this.stack = stack;
	}

	@Override
	public IMessage onMessage(final LimitMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileKabel tile = (TileKabel) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
				tile.setLimit(message.limit);
				tile.setStack(message.stack);
				tile.markDirty();
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.limit = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeInt(this.limit);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}

}
