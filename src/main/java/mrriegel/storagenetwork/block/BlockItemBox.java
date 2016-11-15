package mrriegel.storagenetwork.block;

import java.util.List;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.ModConfig;
import mrriegel.storagenetwork.tile.TileItemBox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class BlockItemBox extends CommonBlockContainer<TileItemBox> {

	public BlockItemBox() {
		super(Material.IRON, "block_item_box");
		setHardness(2.5F);
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileItemBox();
	}

	@Override
	protected Class<? extends TileItemBox> getTile() {
		return TileItemBox.class;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if (NBTStackHelper.getBoolean(stack, "idatakeeper")) {
			ItemStackHandler handler = new ItemStackHandler(ModConfig.itemboxCapacity);
			handler.deserializeNBT(NBTStackHelper.getTag(stack, "storage"));
			int x = 0;
			for (int i = 0; i < handler.getSlots(); i++)
				if (handler.getStackInSlot(i) != null)
					x++;
			tooltip.add(x + "/" + ModConfig.itemboxCapacity + " Items");
		}
	}

}
