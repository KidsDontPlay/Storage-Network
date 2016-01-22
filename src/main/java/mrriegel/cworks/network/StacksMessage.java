package mrriegel.cworks.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mrriegel.cworks.gui.GuiRequest;
import mrriegel.cworks.helper.StackWrapper;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileMaster;
import mrriegel.cworks.tile.TileRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StacksMessage implements IMessage,
		IMessageHandler<StacksMessage, IMessage> {
	int size;
	List<StackWrapper> stacks;

	public StacksMessage() {
	}

	public StacksMessage(List<StackWrapper> stacks) {
		super();
		this.stacks = stacks;
		this.size = stacks.size();
	}

	@Override
	public IMessage onMessage(final StacksMessage message,
			final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft().currentScreen instanceof GuiRequest) {
					GuiRequest gui = (GuiRequest) Minecraft.getMinecraft().currentScreen;
					gui.stacks = message.stacks;
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.size = buf.readInt();
		stacks = new ArrayList<StackWrapper>();
		for (int i = 0; i < size; i++) {
			NBTTagCompound compound = ByteBufUtils.readTag(buf);
			StackWrapper w = new StackWrapper(null, 0);
			w.readFromNBT(compound);
			stacks.add(w);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.size);
		for (StackWrapper w : stacks) {
			NBTTagCompound compound = new NBTTagCompound();
			w.writeToNBT(compound);
			ByteBufUtils.writeTag(buf, compound);
		}
	}
}
