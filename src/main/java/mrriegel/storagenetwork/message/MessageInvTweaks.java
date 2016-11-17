package mrriegel.storagenetwork.message;

import mrriegel.limelib.network.AbstractMessage;
import mrriegel.storagenetwork.container.ContainerAbstractRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class MessageInvTweaks extends AbstractMessage<MessageInvTweaks> {

	public MessageInvTweaks() {
	}

	public MessageInvTweaks(boolean space, boolean shift, boolean ctrl) {
		nbt.setBoolean("space", space);
		nbt.setBoolean("shift", shift);
		nbt.setBoolean("ctrl", ctrl);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		if (player.openContainer instanceof ContainerAbstractRequest) {
			((ContainerAbstractRequest) player.openContainer).ctrl = nbt.getBoolean("ctrl");
			((ContainerAbstractRequest) player.openContainer).shift = nbt.getBoolean("shift");
			((ContainerAbstractRequest) player.openContainer).space = nbt.getBoolean("space");
		}
	}

}
