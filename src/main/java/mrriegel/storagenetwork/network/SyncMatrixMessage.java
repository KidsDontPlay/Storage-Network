package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.ContainerRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncMatrixMessage implements IMessage,
		IMessageHandler<SyncMatrixMessage, IMessage> {
	ItemStack stack;
	int slot;

	public SyncMatrixMessage() {
	}

	public SyncMatrixMessage(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
	}

	@Override
	public IMessage onMessage(final SyncMatrixMessage message,
			final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerRequest) {
					ContainerRequest con = ((ContainerRequest) Minecraft
							.getMinecraft().thePlayer.openContainer);
					con.craftMatrix.setInventorySlotContents(message.slot,
							message.stack);
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.stack = ByteBufUtils.readItemStack(buf);
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, this.stack);
		buf.writeInt(this.slot);
	}
}
