package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.helper.NBTHelper;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TemplateMessage implements IMessage, IMessageHandler<TemplateMessage, IMessage> {
	int index;
	boolean ore, meta;

	public TemplateMessage() {
	}

	public TemplateMessage(int index, boolean ore, boolean meta) {
		this.index = index;
		this.ore = ore;
		this.meta = meta;
	}

	@Override
	public IMessage onMessage(final TemplateMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				NBTHelper.setBoolean(ctx.getServerHandler().playerEntity.inventory.getCurrentItem(), "ore" + message.index, message.ore);
				NBTHelper.setBoolean(ctx.getServerHandler().playerEntity.inventory.getCurrentItem(), "meta" + message.index, message.meta);
				ctx.getServerHandler().playerEntity.inventory.mainInventory[ctx.getServerHandler().playerEntity.inventory.currentItem] = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.index = buf.readInt();
		this.ore = buf.readBoolean();
		this.meta = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.index);
		buf.writeBoolean(this.ore);
		buf.writeBoolean(this.meta);
	}
}
