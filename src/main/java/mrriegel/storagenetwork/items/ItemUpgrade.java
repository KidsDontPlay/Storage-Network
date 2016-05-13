package mrriegel.storagenetwork.items;

import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgrade extends Item {
	public static final int NUM = 4;
	public static final int SPEED = 0;
	public static final int OP = 1;
	public static final int STACK = 2;
	public static final int STOCK = 3;

	public ItemUpgrade() {
		super();
		this.setCreativeTab(StorageNetwork.tab1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName(StorageNetwork.MODID + ":upgrade");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < NUM; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + "_" + stack.getItemDamage();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(StatCollector.translateToLocal("tooltip.storagenetwork.upgrade_" + stack.getItemDamage()));
	}
}
