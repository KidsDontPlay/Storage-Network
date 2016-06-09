package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mrriegel.storagenetwork.gui.AbstractGuiRequest;
import mrriegel.storagenetwork.helper.StackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.google.common.collect.Lists;

public class StacksMessage implements IMessage, IMessageHandler<StacksMessage, IMessage> {
	int size, csize;
	List<StackWrapper> stacks, craftableStacks;

	public StacksMessage() {
	}

	public StacksMessage(List<StackWrapper> stacks, List<StackWrapper> craftableStacks) {
		super();
		this.stacks = stacks;
		this.craftableStacks = craftableStacks;
		this.size = stacks.size();
		this.csize = craftableStacks.size();
	}

	@Override
	public IMessage onMessage(final StacksMessage message, final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft().currentScreen instanceof AbstractGuiRequest) {
					AbstractGuiRequest gui = (AbstractGuiRequest) Minecraft.getMinecraft().currentScreen;
					gui.stacks = message.stacks;
					gui.craftableStacks = message.craftableStacks;
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.size = buf.readInt();
		this.csize = buf.readInt();
		stacks = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			stacks.add(StackWrapper.loadStackWrapperFromNBT(ByteBufUtils.readTag(buf)));
		}
		craftableStacks = Lists.newArrayList();
		for (int i = 0; i < csize; i++) {
			craftableStacks.add(StackWrapper.loadStackWrapperFromNBT(ByteBufUtils.readTag(buf)));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.size);
		buf.writeInt(this.csize);
		for (StackWrapper w : stacks) {
			NBTTagCompound compound = new NBTTagCompound();
			w.writeToNBT(compound);
			ByteBufUtils.writeTag(buf, compound);
		}
		for (StackWrapper w : craftableStacks) {
			NBTTagCompound compound = new NBTTagCompound();
			w.writeToNBT(compound);
			ByteBufUtils.writeTag(buf, compound);
		}
	}
}
