package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.frequest.ContainerFRequest;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemHandlerHelper;

public class InsertMessage implements IMessage, IMessageHandler<InsertMessage, IMessage> {
	int dim;
	ItemStack stack;

	public InsertMessage() {
	}

	public InsertMessage(int dim, ItemStack stack) {
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
				TileEntity t = null;
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRequest)
					t = w.getTileEntity(((ContainerRequest) ctx.getServerHandler().playerEntity.openContainer).tile.getMaster());
				else if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerFRequest)
					t = w.getTileEntity(((ContainerFRequest) ctx.getServerHandler().playerEntity.openContainer).tile.getMaster());
				if (t instanceof TileMaster) {
					TileMaster tile = (TileMaster) t;
					int rest = tile.insertStack(message.stack, null, false);
					ItemStack send = null;
					if (rest != 0)
						send = ItemHandlerHelper.copyStackWithSize(message.stack, rest);
					ctx.getServerHandler().playerEntity.inventory.setItemStack(send);
					PacketHandler.INSTANCE.sendTo(new StackMessage(send), ctx.getServerHandler().playerEntity);
					PacketHandler.INSTANCE.sendTo(new StacksMessage(tile.getStacks(), tile.getCraftableStacks()), ctx.getServerHandler().playerEntity);
					ctx.getServerHandler().playerEntity.openContainer.detectAndSendChanges();
				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.dim = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.dim);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}
}
