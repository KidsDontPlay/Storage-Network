package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncMessage implements IMessage,
		IMessageHandler<SyncMessage, IMessage> {
	ItemStack a, b, c, d, e, f;

	public SyncMessage() {
	}

	public SyncMessage(ItemStack a, ItemStack b, ItemStack c, ItemStack d,
			ItemStack e, ItemStack f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}

	@Override
	public IMessage onMessage(final SyncMessage message,
			final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerRequest) {
					ContainerRequest con = ((ContainerRequest) Minecraft
							.getMinecraft().thePlayer.openContainer);
					con.back.setInventorySlotContents(0, message.a);
					con.back.setInventorySlotContents(1, message.b);
					con.back.setInventorySlotContents(2, message.c);
					con.back.setInventorySlotContents(3, message.d);
					con.back.setInventorySlotContents(4, message.e);
					con.back.setInventorySlotContents(5, message.f);
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.a = ByteBufUtils.readItemStack(buf);
		this.b = ByteBufUtils.readItemStack(buf);
		this.c = ByteBufUtils.readItemStack(buf);
		this.d = ByteBufUtils.readItemStack(buf);
		this.e = ByteBufUtils.readItemStack(buf);
		this.f = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, this.a);
		ByteBufUtils.writeItemStack(buf, this.b);
		ByteBufUtils.writeItemStack(buf, this.c);
		ByteBufUtils.writeItemStack(buf, this.d);
		ByteBufUtils.writeItemStack(buf, this.e);
		ByteBufUtils.writeItemStack(buf, this.f);
	}
}
