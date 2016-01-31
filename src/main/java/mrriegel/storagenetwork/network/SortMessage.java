package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.remote.ContainerRemote;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.tile.TileRequest;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SortMessage implements IMessage,
		IMessageHandler<SortMessage, IMessage> {
	int x, y, z;
	boolean direction;
	Sort sort;

	public SortMessage() {
	}

	public SortMessage(int x, int y, int z, boolean direction, Sort sort) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
		this.sort = sort;
	}

	@Override
	public IMessage onMessage(final SortMessage message,
			final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRemote) {
					ItemStack s = ctx.getServerHandler().playerEntity
							.getHeldItem();
					NBTHelper.setBoolean(s, "down", message.direction);
					NBTHelper.setString(s, "sort", message.sort.toString());
					return;
				}
				TileRequest tile = (TileRequest) ctx.getServerHandler().playerEntity.worldObj
						.getTileEntity(new BlockPos(message.x, message.y,
								message.z));
				tile.sort = message.sort;
				tile.downwards = message.direction;
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.direction = buf.readBoolean();
		this.sort = Sort.valueOf(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeBoolean(this.direction);
		ByteBufUtils.writeUTF8String(buf, this.sort.toString());
	}

}
