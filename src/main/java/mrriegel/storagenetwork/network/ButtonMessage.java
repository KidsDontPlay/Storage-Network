package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.tile.AbstractFilterTile;
import mrriegel.storagenetwork.tile.TileIndicator;
import mrriegel.storagenetwork.tile.TileKabel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ButtonMessage implements IMessage, IMessageHandler<ButtonMessage, IMessage> {
	int id;
	BlockPos pos;

	public ButtonMessage() {
	}

	public ButtonMessage(int id, BlockPos pos) {
		this.id = id;
		this.pos = pos;
	}

	@Override
	public IMessage onMessage(final ButtonMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileEntity t = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
				if (t instanceof AbstractFilterTile) {
					AbstractFilterTile tile = (AbstractFilterTile) t;
					switch (message.id) {
					case 0:
						tile.setPriority(tile.getPriority() - 1);
						break;
					case 1:
						tile.setPriority(tile.getPriority() + 1);
						break;
					case 3:
						tile.setWhite(!tile.isWhite());
						break;
					case 4:
						if (tile instanceof TileKabel)
							((TileKabel) tile).setMode(!((TileKabel) tile).isMode());
						break;
					}
				} else if (t instanceof TileIndicator) {
					TileIndicator tile = (TileIndicator) t;
					tile.setMore(!tile.isMore());
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeInt(this.id);
	}

}
