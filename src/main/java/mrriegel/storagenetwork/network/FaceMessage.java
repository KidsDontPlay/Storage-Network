package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.container.GuiContainer;
import mrriegel.storagenetwork.tile.TileContainer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FaceMessage implements IMessage, IMessageHandler<FaceMessage, IMessage> {
	int id;
	BlockPos pos;

	public FaceMessage() {
	}

	public FaceMessage(int id, BlockPos pos) {
		this.id = id;
		this.pos = pos;
	}

	@Override
	public IMessage onMessage(final FaceMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileContainer tile = (TileContainer) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
				switch (message.id) {
				case 0:
					tile.setInput(GuiContainer.next(tile.getInput()));
					break;
				case 1:
					tile.setOutput(GuiContainer.next(tile.getOutput()));
					break;
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
