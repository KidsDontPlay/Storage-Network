package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mrriegel.storagenetwork.gui.AbstractGuiFRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.google.common.collect.Lists;

public class FluidsMessage implements IMessage, IMessageHandler<FluidsMessage, IMessage> {
	int size;
	List<FluidStack> stacks;

	public FluidsMessage() {
	}

	public FluidsMessage(List<FluidStack> stacks) {
		super();
		this.stacks = stacks;
		this.size = stacks.size();
	}

	@Override
	public IMessage onMessage(final FluidsMessage message, final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft().currentScreen instanceof AbstractGuiFRequest) {
					AbstractGuiFRequest gui = (AbstractGuiFRequest) Minecraft.getMinecraft().currentScreen;
					gui.fluids = message.stacks;
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.size = buf.readInt();
		stacks = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			NBTTagCompound compound = ByteBufUtils.readTag(buf);
			FluidStack w = FluidStack.loadFluidStackFromNBT(compound);
			stacks.add(w);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.size);
		for (FluidStack w : stacks) {
			NBTTagCompound compound = new NBTTagCompound();
			w.writeToNBT(compound);
			ByteBufUtils.writeTag(buf, compound);
		}
	}
}
