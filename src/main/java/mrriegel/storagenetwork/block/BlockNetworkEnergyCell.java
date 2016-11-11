package mrriegel.storagenetwork.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileNetworkEnergyCell;

public class BlockNetworkEnergyCell extends CommonBlockContainer<TileNetworkEnergyCell>{

	public BlockNetworkEnergyCell() {
		super(Material.IRON, "block_network_energy_cell");
		setHardness(2.5F);
        setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileNetworkEnergyCell();
	}

	@Override
	protected Class<? extends TileNetworkEnergyCell> getTile() {
		return TileNetworkEnergyCell.class;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote)
		playerIn.addChatMessage(new TextComponentString(((TileNetworkEnergyCell)worldIn.getTileEntity(pos)).getEnergy().getEnergyStored()+" RF"));
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(NBTStackHelper.getBoolean(stack, "idatakeeper"))
			tooltip.add(NBTStackHelper.getInt(stack, "energy")+" RF");
		super.addInformation(stack, playerIn, tooltip, advanced);
	}

}
