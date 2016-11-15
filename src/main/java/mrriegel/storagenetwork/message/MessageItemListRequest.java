package mrriegel.storagenetwork.message;

import java.util.List;

import mrriegel.limelib.network.AbstractMessage;
import mrriegel.limelib.util.StackWrapper;
import mrriegel.storagenetwork.gui.GuiRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Lists;

public class MessageItemListRequest extends AbstractMessage<MessageItemListRequest> {

	public MessageItemListRequest() {
	}

	public MessageItemListRequest(List<ItemStack> stacks) {
		List<StackWrapper> lis = StackWrapper.toWrapperList(stacks);
		nbt.setInteger("size", lis.size());
		for (int i = 0; i < lis.size(); i++) {
			NBTTagCompound n = new NBTTagCompound();
			lis.get(i).writeToNBT(n);
			nbt.setTag(i + "", n);
		}
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		List<StackWrapper> lis = Lists.newArrayList();
		for (int i = 0; i < nbt.getInteger("size"); i++) {
			lis.add(StackWrapper.loadStackWrapperFromNBT(nbt.getCompoundTag("" + i)));
		}
		if (Minecraft.getMinecraft().currentScreen instanceof GuiRequest) {
			((GuiRequest) Minecraft.getMinecraft().currentScreen).wrappers = lis;
		}

	}

}
