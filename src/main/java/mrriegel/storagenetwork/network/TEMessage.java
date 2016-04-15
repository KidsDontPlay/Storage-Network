package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TEMessage implements IMessage {

	BlockPos p;
	NBTTagCompound nbt;

	public TEMessage() {
	}

	public TEMessage(TileEntity tile) {
		p = tile.getPos();
		nbt = tile.serializeNBT();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		p = BlockPos.fromLong(buf.readLong());
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(p.toLong());
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class Handler implements IMessageHandler<TEMessage, IMessage> {

		@Override
		public IMessage onMessage(final TEMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Minecraft.getMinecraft().theWorld.getTileEntity(message.p).deserializeNBT(message.nbt);
				}
			});
			return null;
		}

	}

}
