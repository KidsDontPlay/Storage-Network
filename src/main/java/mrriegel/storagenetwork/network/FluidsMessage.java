package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import mrriegel.storagenetwork.gui.frequest.GuiFRequest;
import mrriegel.storagenetwork.gui.remote.GuiRemote;
import mrriegel.storagenetwork.handler.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FluidsMessage implements IMessage, IMessageHandler<FluidsMessage, IMessage> {
	int size, id;
	List<FluidStack> stacks;

	public FluidsMessage() {
	}

	public FluidsMessage(List<FluidStack> stacks, int id) {
		super();
		this.stacks = stacks;
		this.size = stacks.size();
		this.id = id;
	}

	@Override
	public IMessage onMessage(final FluidsMessage message, final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (message.id == GuiHandler.REMOTE && Minecraft.getMinecraft().currentScreen instanceof GuiRemote) {
					GuiRemote gui = (GuiRemote) Minecraft.getMinecraft().currentScreen;
					// gui.stacks = message.stacks;
				} else if (message.id == GuiHandler.FREQUEST && Minecraft.getMinecraft().currentScreen instanceof GuiFRequest) {
					GuiFRequest gui = (GuiFRequest) Minecraft.getMinecraft().currentScreen;
					gui.fluids = message.stacks;
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.size = buf.readInt();
		this.id = buf.readInt();
		stacks = new ArrayList<FluidStack>();
		for (int i = 0; i < size; i++) {
			NBTTagCompound compound = ByteBufUtils.readTag(buf);
			FluidStack w = FluidStack.loadFluidStackFromNBT(compound);
			stacks.add(w);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.size);
		buf.writeInt(this.id);
		for (FluidStack w : stacks) {
			NBTTagCompound compound = new NBTTagCompound();
			w.writeToNBT(compound);
			ByteBufUtils.writeTag(buf, compound);
		}
	}
}
