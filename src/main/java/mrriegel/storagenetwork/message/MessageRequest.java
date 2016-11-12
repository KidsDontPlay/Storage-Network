package mrriegel.storagenetwork.message;

import mrriegel.limelib.network.AbstractMessage;
import mrriegel.limelib.util.FilterItem;
import mrriegel.storagenetwork.container.ContainerRequestTable;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class MessageRequest extends AbstractMessage<MessageRequest>{
	
	public MessageRequest() {
	}

	public MessageRequest(ItemStack stack,int mouseButton,boolean shift,boolean ctrl) {
		if(stack!=null)
			stack.writeToNBT(nbt);
		nbt.setInteger("mouse", mouseButton);
		nbt.setBoolean("shift", shift);
		nbt.setBoolean("ctrl", ctrl);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		if(player.openContainer instanceof ContainerRequestTable)
		{
			TileNetworkCore core=((ContainerRequestTable)player.openContainer).tile.getNetworkCore();
			if(core==null)return;
			ItemStack stack=ItemStack.loadItemStackFromNBT(nbt);
			if(stack!=null){
			int mouse=nbt.getInteger("mouse");
			int size=nbt.getBoolean("ctrl")?1:mouse==1?stack.getMaxStackSize()/2:mouse==0?stack.getMaxStackSize():0;
			ItemStack req=core.network.requestItem(new FilterItem(stack, true, false, true), size, false);
			}}
	}
}
