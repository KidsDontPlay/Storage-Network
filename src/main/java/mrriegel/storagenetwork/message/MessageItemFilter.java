package mrriegel.storagenetwork.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.network.AbstractMessage;
import mrriegel.storagenetwork.Registry;

public class MessageItemFilter extends AbstractMessage<MessageItemFilter> {

	public MessageItemFilter() {
	}

	public MessageItemFilter(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		if (player.getHeldItemMainhand() != null) {
			ItemStack stack = player.getHeldItemMainhand();
			if (stack.getItem() == Registry.itemFilter) {
				switch (nbt.getInteger("buttonID")) {
				case 0:
					NBTStackHelper.setBoolean(stack, "meta", !NBTStackHelper.getBoolean(stack, "meta"));
					break;
				case 1:
					NBTStackHelper.setBoolean(stack, "nbt", !NBTStackHelper.getBoolean(stack, "nbt"));
					break;
				case 2:
					NBTStackHelper.setBoolean(stack, "ore", !NBTStackHelper.getBoolean(stack, "ore"));
					break;
				case 3:
					NBTStackHelper.setBoolean(stack, "mod", !NBTStackHelper.getBoolean(stack, "mod"));
					break;
				case 4:
					NBTStackHelper.setBoolean(stack, "white", !NBTStackHelper.getBoolean(stack, "white"));
					break;
				default:
					break;
				}
			}
		}
	}

}
