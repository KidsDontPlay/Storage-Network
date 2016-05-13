package mrriegel.storagenetwork.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.handler.GuiHandler;

public class ItemFRemote extends ItemRemote {

	public ItemFRemote() {
		super();
		this.setUnlocalizedName(StorageNetwork.MODID + ":fremote");
	}

	@Override
	protected int getGui() {
		return GuiHandler.FREMOTE;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.fremote_"+stack.getItemDamage()));
	}
}
