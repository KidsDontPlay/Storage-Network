package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClearMessage implements IMessage, IMessageHandler<ClearMessage, IMessage> {

	@Override
	public IMessage onMessage(final ClearMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRequest) {
					ContainerRequest c = (ContainerRequest) ctx.getServerHandler().playerEntity.openContainer;
					for (int i = 0; i < 9; i++) {
						World w = ctx.getServerHandler().playerEntity.worldObj;
						ItemStack s = c.craftMatrix.getStackInSlot(i);
						if (s == null)
							continue;
						int num = s.stackSize;
						int rest = ((TileMaster) w.getTileEntity(c.tile.getMaster())).insertStack(s.copy(), null);
						if (num == rest)
							continue;
						if (rest == 0)
							c.craftMatrix.setInventorySlotContents(i, null);
						else
							c.craftMatrix.setInventorySlotContents(i, Inv.copyStack(s, rest));
						PacketHandler.INSTANCE.sendTo(new StacksMessage(((TileMaster) w.getTileEntity(c.tile.getMaster())).getStacks(),((TileMaster) w.getTileEntity(c.tile.getMaster())).getCraftableStacks(), GuiHandler.REQUEST), ctx.getServerHandler().playerEntity);
					}

				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

}
