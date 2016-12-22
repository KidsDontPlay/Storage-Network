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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
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

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && (heldItem == null || !(heldItem.getItem() instanceof ItemBlock))) {
			TileItemBox box = (TileItemBox) worldIn.getTileEntity(pos);
			IItemHandler handler = box.getStorage();
			int x = 0;
			for (int i = 0; i < handler.getSlots(); i++)
				if (handler.getStackInSlot(i) != null)
					x++;
			playerIn.addChatMessage(new TextComponentString(x + "/" + ModConfig.itemboxCapacity + " Items"));
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

}
