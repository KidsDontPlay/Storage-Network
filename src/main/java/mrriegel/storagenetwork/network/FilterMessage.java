package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.cable.ContainerCable;
import mrriegel.storagenetwork.gui.cable.ContainerFCable;
import mrriegel.storagenetwork.helper.StackWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FilterMessage implements IMessage, IMessageHandler<FilterMessage, IMessage> {
	int index;
	StackWrapper wrap;
	boolean ore, meta;

	public FilterMessage() {
	}

	public FilterMessage(int index, StackWrapper wrap, boolean ore, boolean meta) {
		this.index = index;
		this.wrap = wrap;
		this.ore = ore;
		this.meta = meta;
	}

	@Override
	public IMessage onMessage(final FilterMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerCable) {
					ContainerCable con = (ContainerCable) ctx.getServerHandler().playerEntity.openContainer;
					con.getFilter().put(message.index, message.wrap);
					con.getOres().put(message.index, message.ore);
					con.getMetas().put(message.index, message.meta);
					con.slotChanged();
				} else if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerFCable) {
					ContainerFCable con = (ContainerFCable) ctx.getServerHandler().playerEntity.openContainer;
					con.getFilter().put(message.index, message.wrap);
					con.slotChanged();
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.index = buf.readInt();
		this.ore = buf.readBoolean();
		this.meta = buf.readBoolean();
		this.wrap = StackWrapper.loadStackWrapperFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.index);
		buf.writeBoolean(this.ore);
		buf.writeBoolean(this.meta);
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.wrap != null)
			this.wrap.writeToNBT(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}
}
