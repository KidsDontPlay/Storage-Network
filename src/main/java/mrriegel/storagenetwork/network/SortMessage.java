package mrriegel.storagenetwork.network;

import io.netty.buffer.ByteBuf;
import mrriegel.storagenetwork.gui.fremote.ContainerFRemote;
import mrriegel.storagenetwork.gui.remote.ContainerRemote;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.tile.TileFRequest;
import mrriegel.storagenetwork.tile.TileRequest;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SortMessage implements IMessage, IMessageHandler<SortMessage, IMessage> {
	BlockPos pos;
	boolean direction;
	Sort sort;

	public SortMessage() {
	}

	public SortMessage(BlockPos pos, boolean direction, Sort sort) {
		this.pos = pos;
		this.direction = direction;
		this.sort = sort;
	}

	@Override
	public IMessage onMessage(final SortMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerRemote || ctx.getServerHandler().playerEntity.openContainer instanceof ContainerFRemote) {
					ItemStack s = ctx.getServerHandler().playerEntity.getHeldItem();
					NBTHelper.setBoolean(s, "down", message.direction);
					NBTHelper.setString(s, "sort", message.sort.toString());
					return;

				}
				TileEntity t = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
				if (t instanceof TileRequest) {
					TileRequest tile = (TileRequest) t;
					tile.sort = message.sort;
					tile.downwards = message.direction;
				} else if (t instanceof TileFRequest) {
					TileFRequest tile = (TileFRequest) t;
					tile.sort = message.sort;
					tile.downwards = message.direction;
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.direction = buf.readBoolean();
		this.sort = Sort.valueOf(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeBoolean(this.direction);
		ByteBufUtils.writeUTF8String(buf, this.sort.toString());
	}

}
