package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.fremote.ContainerFRemote;
import mrriegel.storagenetwork.gui.frequest.ContainerFRequest;
import mrriegel.storagenetwork.handler.GuiHandler;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FRequestMessage implements IMessage, IMessageHandler<FRequestMessage, IMessage> {
	int id;
	Fluid fluid;

	public FRequestMessage() {
	}

	public FRequestMessage(int id, Fluid fluid) {
		this.id = id;
		this.fluid = fluid;
	}

	@Override
	public IMessage onMessage(final FRequestMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerFRequest) {
					TileMaster tile = (TileMaster) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(((ContainerFRequest) ctx.getServerHandler().playerEntity.openContainer).tile.getMaster());
					if (tile == null)
						return;
					ContainerFRequest con = ((ContainerFRequest) ctx.getServerHandler().playerEntity.openContainer);
					ItemStack fill = con.tile.fill;


					if(message.id < 2 && message.fluid != null && fill != null && FluidUtil.getFluidHandler(fill) != null){
						int space = FluidUtil.getFluidHandler(fill).fill(new FluidStack(message.fluid, 1000), false);
						if(space == 1000){
							if(FluidUtil.getFluidContained(fill) == null || FluidUtil.getFluidContained(fill).getFluid() == message.fluid){
								FluidStack fluid = tile.frequest(message.fluid, space, true);
								if (fluid != null) {
									FluidUtil.getFluidHandler(fill).fill(new FluidStack(message.fluid, fluid.amount), true);
									tile.frequest(message.fluid, fluid.amount, false);
									con.inv.setInventorySlotContents(0, fill);
									con.slotChanged();
								}
							}
						}
					}

					/*
					if (message.id < 2 && message.fluid != null && fill != null && FluidContainerRegistry.isEmptyContainer(fill)) {
						int space = FluidContainerRegistry.isBucket(fill) ? FluidContainerRegistry.BUCKET_VOLUME : FluidContainerRegistry.getContainerCapacity(new FluidStack(message.fluid, 1), fill);
						boolean canFill = FluidContainerRegistry.fillFluidContainer(new FluidStack(message.fluid, space), fill.copy()) != null;
						if (space > 0 && canFill) {
							FluidStack fluid = tile.frequest(message.fluid, space, true);
							if (fluid != null) {
								ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(message.fluid, fluid.amount), fill);
								if (filled != null) {
									tile.frequest(message.fluid, fluid.amount, false);
									con.inv.setInventorySlotContents(0, filled);
									con.slotChanged();
								}
							}
						}
					} else if (message.id < 2 && message.fluid != null && fill != null && fill.getItem() instanceof IFluidContainerItem) {
						IFluidContainerItem flui = (IFluidContainerItem) fill.getItem();
						if (flui.getFluid(fill) == null || flui.getFluid(fill).getFluid() == message.fluid) {
							int space = flui.getFluid(fill) == null ? flui.getCapacity(fill) : flui.getCapacity(fill) - flui.getFluid(fill).amount;
							space = Math.min(space, message.id == 0 ? 1000 : message.id == 1 ? 100 : 0);
							if (space > 0) {
								FluidStack fluid = tile.frequest(message.fluid, space, false);
								if (fluid != null) {
									flui.fill(fill, fluid, true);
									con.inv.setInventorySlotContents(0, fill);
									con.slotChanged();
								}
							}
						}
					}
					*/
					PacketHandler.INSTANCE.sendTo(new FluidsMessage(tile.getFluids(), GuiHandler.FREQUEST), ctx.getServerHandler().playerEntity);

				} else if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerFRemote) {
					TileMaster tile = ItemRemote.getTile(ctx.getServerHandler().playerEntity.inventory.getCurrentItem());
					if (tile == null)
						return;
					ContainerFRemote con = ((ContainerFRemote) ctx.getServerHandler().playerEntity.openContainer);
					ItemStack fill = con.inv.getStackInSlot(0);
					if (tile != null && message.id < 2 && message.fluid != null && fill != null && FluidContainerRegistry.isEmptyContainer(fill)) {
						int space = FluidContainerRegistry.isBucket(fill) ? FluidContainerRegistry.BUCKET_VOLUME : FluidContainerRegistry.getContainerCapacity(new FluidStack(message.fluid, 1), fill);
						boolean canFill = FluidContainerRegistry.fillFluidContainer(new FluidStack(message.fluid, space), fill.copy()) != null;
						if (space > 0 && canFill) {
							FluidStack fluid = tile.frequest(message.fluid, space, true);
							if (fluid != null) {
								ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(message.fluid, fluid.amount), fill);
								if (filled != null) {
									tile.frequest(message.fluid, fluid.amount, false);
									con.inv.setInventorySlotContents(0, filled);
								}
							}
						}
					} else if (tile != null && message.id < 2 && message.fluid != null && fill != null && fill.getItem() instanceof IFluidContainerItem) {
						IFluidContainerItem flui = (IFluidContainerItem) fill.getItem();
						if (flui.getFluid(fill) == null || flui.getFluid(fill).getFluid() == message.fluid) {
							int space = flui.getFluid(fill) == null ? flui.getCapacity(fill) : flui.getCapacity(fill) - flui.getFluid(fill).amount;
							space = Math.min(space, message.id == 0 ? 1000 : message.id == 1 ? 100 : 0);
							if (space > 0) {
								FluidStack fluid = tile.frequest(message.fluid, space, false);
								if (fluid != null) {
									flui.fill(fill, fluid, true);
									con.inv.setInventorySlotContents(0, fill);
								}
							}
						}
					}
					PacketHandler.INSTANCE.sendTo(new FluidsMessage(tile.getFluids(), GuiHandler.FREMOTE), ctx.getServerHandler().playerEntity);

				}

			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.fluid = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		if (fluid != null)
			ByteBufUtils.writeUTF8String(buf, this.fluid.getName());
		else
			ByteBufUtils.writeUTF8String(buf, "");
	}
}
