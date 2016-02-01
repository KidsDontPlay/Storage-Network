package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StackMessage implements IMessage,
		IMessageHandler<StackMessage, IMessage> {
	ItemStack a;

	public StackMessage() {
	}

	public StackMessage(ItemStack a) {
		this.a = a;
	}

	@Override
	public IMessage onMessage(final StackMessage message,
			final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Minecraft.getMinecraft().thePlayer.inventory
						.setItemStack(message.a);

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.a = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, this.a);
	}
}
