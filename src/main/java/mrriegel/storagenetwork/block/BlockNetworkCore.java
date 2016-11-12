package mrriegel.storagenetwork.block;

import java.util.Random;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.item.CommonItemBlock;
import mrriegel.limelib.util.StackWrapper;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author canitzp
 */
public class BlockNetworkCore extends CommonBlockContainer<TileNetworkCore> {
	
	public static final PropertyBool ACTIVE=PropertyBool.create("on");

    public BlockNetworkCore() {
        super(Material.IRON, "block_network_core");
        setHardness(2.5F);
        setCreativeTab(CreativeTab.TAB);
        setDefaultState(getDefaultState().withProperty(ACTIVE, false));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!worldIn.isRemote) {
			for (EnumFacing face : EnumFacing.VALUES) {
				if (worldIn.getTileEntity(pos.offset(face)) instanceof INetworkPart&&((INetworkPart) worldIn.getTileEntity(pos.offset(face))).getNeighborFaces().contains(face.getOpposite()) && ((INetworkPart) worldIn.getTileEntity(pos.offset(face))).getNetworkCore() != null) {
					worldIn.setBlockToAir(pos);
					worldIn.playEvent(2001, pos, Block.getIdFromBlock(Registry.networkCore));
					if (!(placer instanceof EntityPlayer) || !((EntityPlayer) placer).isCreative())
						StackHelper.spawnItemStack(worldIn, pos, new ItemStack(Registry.networkCore));
					return;
				}
			}
			TileNetworkCore tile = (TileNetworkCore) worldIn.getTileEntity(pos);
			tile.initializeNetwork();
		}
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		for (EnumFacing face : EnumFacing.VALUES) {
			if (worldIn.getTileEntity(pos.offset(face)) instanceof INetworkPart) {
				INetworkPart part = (INetworkPart) worldIn.getTileEntity(pos.offset(face));
				if (part.getNetworkCore() != null && part.getNetworkCore().getPos().equals(pos))
//		TODO enable			BlockNetworkCable.releaseNetworkParts(worldIn, part.getPosition().getPos());
				;
				else if(part.getNetworkCore() != null && !part.getNetworkCore().getPos().equals(pos))
					part.getNetworkCore().markForNetworkInit();
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
	
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote){
//        	System.out.println("Network size: "+((TileNetworkCore)worldIn.getTileEntity(pos)).network.networkParts.size());
//            System.out.println(((TileNetworkCore)worldIn.getTileEntity(pos)).network);
            ((TileNetworkCore)worldIn.getTileEntity(pos)).sync();
            worldIn.setBlockState(pos, state.withProperty(ACTIVE, !state.getValue(ACTIVE)), 2);
            System.out.println(StackWrapper.toWrapperList(((TileNetworkCore)worldIn.getTileEntity(pos)).network.getItemstacks()));
        }
        playerIn.openGui(StorageNetwork.instance, GuiID.NETWORK_CORE.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
    	super.randomTick(worldIn, pos, state, random);
    }
    
    @Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ACTIVE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ACTIVE, meta > 0);
	}

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNetworkCore();
    }

    @Override
    protected Class<? extends TileNetworkCore> getTile() {
        return TileNetworkCore.class;
    }
    
	@Override
	protected ItemBlock getItemBlock() {
		return new CommonItemBlock(this) {
			@Override
			public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
				BlockPos result = (!block.isReplaceable(worldIn, pos)) ? pos.offset(facing) : pos;
				for (EnumFacing face : EnumFacing.VALUES) {
					if (worldIn.getTileEntity(result.offset(face)) instanceof INetworkPart && ((INetworkPart) worldIn.getTileEntity(result.offset(face))).getNeighborFaces().contains(face.getOpposite()) && ((INetworkPart) worldIn.getTileEntity(result.offset(face))).getNetworkCore() != null) {
						if (!worldIn.isRemote)
							playerIn.addChatMessage(new TextComponentString(TextFormatting.DARK_RED + "There is already a network."));
						return EnumActionResult.FAIL;
					}
				}
				return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			}
		};
	}

}
