package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.frequest.ContainerFRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FRequestMessage implements IMessage, IMessageHandler<FRequestMessage, IMessage> {
	int id, x, y, z;
	Fluid fluid;
	boolean shift;

	public FRequestMessage() {
	}

	public FRequestMessage(int id, int x, int y, int z, Fluid fluid, boolean shift) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.fluid = fluid;
		this.shift = shift;
	}

	@Override
	public IMessage onMessage(final FRequestMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerFRequest) {
					TileMaster tile = (TileMaster) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(((ContainerFRequest) ctx.getServerHandler().playerEntity.openContainer).tile.getMaster());
					ContainerFRequest con = ((ContainerFRequest) ctx.getServerHandler().playerEntity.openContainer);
					ItemStack fill = con.tile.fill;
					System.out.println("filL: " + fill);
					if (message.fluid != null && fill != null && FluidContainerRegistry.isContainer(fill) && (FluidContainerRegistry.containsFluid(fill, new FluidStack(message.fluid, 1)) || FluidContainerRegistry.isEmptyContainer(fill))) {
						int space = FluidContainerRegistry.getContainerCapacity(fill) - (FluidContainerRegistry.isEmptyContainer(fill) ? 0 : FluidContainerRegistry.getFluidForFilledItem(fill).amount);
						boolean canFill = FluidContainerRegistry.fillFluidContainer(new FluidStack(message.fluid, space), fill.copy()) != null;
						System.out.println("space: " + space + " " + canFill);
						if (space > 0 && canFill) {
							FluidStack fluid = tile.frequest(message.fluid, space, false);
							System.out.println(fill = FluidContainerRegistry.fillFluidContainer(new FluidStack(message.fluid, space), fill));
						}
					}
					PacketHandler.INSTANCE.sendTo(new FluidsMessage(tile.getFluids(), GuiHandler.FREQUEST), ctx.getServerHandler().playerEntity);

				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.shift = buf.readBoolean();
		this.fluid = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeBoolean(this.shift);
		if (fluid != null)
			ByteBufUtils.writeUTF8String(buf, this.fluid.getName());
		else
			ByteBufUtils.writeUTF8String(buf, "");
	}
}
