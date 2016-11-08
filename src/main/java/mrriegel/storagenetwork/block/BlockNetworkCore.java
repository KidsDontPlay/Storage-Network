package mrriegel.storagenetwork.block;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author canitzp
 */
public class BlockNetworkCore extends CommonBlockContainer<TileNetworkCore> {

    public BlockNetworkCore() {
        super(Material.IRON, "block_network_core");
        setHardness(2.5F);
        setCreativeTab(CreativeTab.TAB);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!worldIn.isRemote) {
			for (EnumFacing face : EnumFacing.VALUES) {
				if (worldIn.getTileEntity(pos.offset(face)) instanceof INetworkPart && ((INetworkPart) worldIn.getTileEntity(pos.offset(face))).getNetworkCore() != null) {
					worldIn.setBlockToAir(pos);
					worldIn.playEvent(2001, pos, Block.getIdFromBlock(Registry.networkCore));
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote){
            System.out.println(((TileNetworkCore)worldIn.getTileEntity(pos)).network);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNetworkCore();
    }

    @Override
    protected Class<? extends TileNetworkCore> getTile() {
        return TileNetworkCore.class;
    }

}
