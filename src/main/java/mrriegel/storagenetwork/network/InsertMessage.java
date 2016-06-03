package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.remote.ContainerRemote;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InsertMessage implements IMessage, IMessageHandler<InsertMessage, IMessage> {
	int dim;
	ItemStack stack;
	BlockPos pos;

	public InsertMessage() {
	}

	public InsertMessage(BlockPos pos, int dim, ItemStack stack) {
		this.pos = pos;
		this.dim = dim;
		this.stack = stack;

	}

	@Override
	public IMessage onMessage(final InsertMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.dim);
				if (w.getTileEntity(message.pos) instanceof TileMaster) {
					TileMaster tile = (TileMaster) w.getTileEntity(message.pos);
					int rest = tile.insertStack(message.stack, null, false);
					if (rest != 0) {
						ctx.getServerHandler().playerEntity.inventory.setItemStack(Inv.copyStack(message.stack, rest));
						PacketHandler.INSTANCE.sendTo(new StackMessage(Inv.copyStack(message.stack, rest)), ctx.getServerHandler().playerEntity);
					} else {
						ctx.getServerHandler().playerEntity.inventory.setItemStack(null);
						PacketHandler.INSTANCE.sendTo(new StackMessage(null), ctx.getServerHandler().playerEntity);
					}
					if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRemote) {
						PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REMOTE), ctx.getServerHandler().playerEntity);
					}
					if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRequest) {
						PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks(), GuiHandler.REQUEST), ctx.getServerHandler().playerEntity);
					}
					ctx.getServerHandler().playerEntity.openContainer.detectAndSendChanges();
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.dim = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeInt(this.dim);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}
}
