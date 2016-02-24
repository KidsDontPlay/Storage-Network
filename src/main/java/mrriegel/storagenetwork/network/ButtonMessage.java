package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ButtonMessage implements IMessage, IMessageHandler<ButtonMessage, IMessage> {
	int id, x, y, z;

	public ButtonMessage() {
	}

	public ButtonMessage(int id, int x, int y, int z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public IMessage onMessage(final ButtonMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileKabel tile = (TileKabel) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
				switch (message.id) {
				case 0:
					tile.setPriority(tile.getPriority() - 1);
					break;
				case 1:
					tile.setPriority(tile.getPriority() + 1);
					break;
				// case 2:
				// tile.setMeta(!tile.isMeta());
				// break;
				case 3:
					tile.setWhite(!tile.isWhite());
					break;
				case 4:
					tile.setMode(!tile.isMode());
					break;
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.id);
	}

}
