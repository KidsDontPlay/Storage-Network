package mrriegel.storagenetwork.network;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.cable.ContainerCable;
import mrriegel.storagenetwork.gui.remote.ContainerRemote;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FilterMessage implements IMessage, IMessageHandler<FilterMessage, IMessage> {
	int index;
	StackWrapper wrap;

	public FilterMessage() {
	}

	public FilterMessage(int index, StackWrapper wrap) {
		this.index = index;
		this.wrap = wrap;
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
					con.slotChanged();
					ctx.getServerHandler().playerEntity.worldObj.markBlockForUpdate(con.tile.getPos());
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.index = buf.readInt();
		this.wrap = StackWrapper.loadStackWrapperFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.index);
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.wrap != null)
			this.wrap.writeToNBT(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}
}
