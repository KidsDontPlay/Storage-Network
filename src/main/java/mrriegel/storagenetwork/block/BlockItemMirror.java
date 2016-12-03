package mrriegel.storagenetwork.block;

import static net.minecraft.block.BlockHorizontal.FACING;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.StackWrapper;
import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.tile.TileItemMirror;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class BlockItemMirror extends CommonBlockContainer<TileItemMirror> {

	public BlockItemMirror() {
		super(Material.IRON, "block_item_mirror");
		setHardness(2.5F);
		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		setCreativeTab(CreativeTab.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileItemMirror();
	}

	@Override
	protected Class<? extends TileItemMirror> getTile() {
		return TileItemMirror.class;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		((TileItemMirror) worldIn.getTileEntity(pos)).face = state.getValue(FACING);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		int h = getQuadrant(state, hitX, hitY, hitZ);
		TileItemMirror tile = (TileItemMirror) worldIn.getTileEntity(pos);
		if (h >= 0 && h <= 3) {
			if (playerIn.isSneaking() && heldItem == null)
				tile.wraps.set(h, null);
			else if (!playerIn.isSneaking() && heldItem == null) {
				if (tile.wraps.get(h) != null && !worldIn.isRemote && tile.getNetworkCore() != null && tile.getNetworkCore().network != null) {
					PlayerMainInvWrapper handler = new PlayerMainInvWrapper(playerIn.inventory);
					for (int i = 0; i < handler.getSlots(); i++) {
						if (new FilterItem(tile.wraps.get(h).getStack()).match(handler.getStackInSlot(i))) {
							ItemStack remain = tile.getNetworkCore().network.insertItem(handler.getStackInSlot(i), null, false);
							handler.setStackInSlot(i, remain);
							if (remain != null)
								break;

						}
					}
					playerIn.openContainer.detectAndSendChanges();
					tile.markForSync();
				}
			} else if (!playerIn.isSneaking() && heldItem != null)
				if (tile.wraps.get(h) == null)
					tile.wraps.set(h, new StackWrapper(heldItem, 0));
				else if (new FilterItem(tile.wraps.get(h).getStack()).match(heldItem) && !worldIn.isRemote && tile.getNetworkCore() != null) {
					ItemStack remain = tile.getNetworkCore().network.insertItem(heldItem, null, false);
					playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = remain;
					playerIn.openContainer.detectAndSendChanges();
					tile.markForSync();
				}
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	public static int getQuadrant(IBlockState state, float hitX, float hitY, float hitZ) {
		int h = 1000;
		switch (state.getValue(FACING)) {
		case EAST:
			if (hitY > .5f && hitZ > .5f)
				h = 0;
			if (hitY > .5f && hitZ < .5f)
				h = 1;
			if (hitY < .5f && hitZ > .5f)
				h = 2;
			if (hitY < .5f && hitZ < .5f)
				h = 3;
			break;
		case NORTH:
			if (hitX > .5 && hitY > .5f)
				h = 0;
			if (hitX < .5f && hitY > .5f)
				h = 1;
			if (hitX > .5f && hitY < .5f)
				h = 2;
			if (hitX < .5f && hitY < .5f)
				h = 3;
			break;
		case SOUTH:
			if (hitX < .5f && hitY > .5f)
				h = 0;
			if (hitX > .5 && hitY > .5f)
				h = 1;
			if (hitX < .5f && hitY < .5f)
				h = 2;
			if (hitX > .5f && hitY < .5f)
				h = 3;
			break;
		case WEST:
			if (hitY > .5f && hitZ < .5f)
				h = 0;
			if (hitY > .5f && hitZ > .5f)
				h = 1;
			if (hitY < .5f && hitZ < .5f)
				h = 2;
			if (hitY < .5f && hitZ > .5f)
				h = 3;
			break;
		default:
			break;
		}
		return h;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		boolean x = super.rotateBlock(world, pos, axis);
		onBlockAdded(world, pos, world.getBlockState(pos));
		return x;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@SubscribeEvent
	public static void leftclick(LeftClickBlock event) {
		if (event.getEntityPlayer() != null) {
			EntityPlayer player = event.getEntityPlayer();
			TileEntity t = event.getWorld().getTileEntity(event.getPos());
			if (t instanceof TileItemMirror) {
				TileItemMirror tile = (TileItemMirror) t;
				if (event.getFace() == tile.face) {
					float f1 = (float) (event.getHitVec().xCoord - event.getPos().getX());
					float f2 = (float) (event.getHitVec().yCoord - event.getPos().getY());
					float f3 = (float) (event.getHitVec().zCoord - event.getPos().getZ());
					int h = getQuadrant(event.getWorld().getBlockState(event.getPos()), f1, f2, f3);
					if (h >= 0 && h <= 3 && tile.wraps.get(h) != null && !event.getWorld().isRemote && tile.getNetworkCore() != null && tile.getNetworkCore().network != null && tile.canExtract()) {
						ItemStack req = tile.getNetworkCore().network.requestItem(new FilterItem(tile.wraps.get(h).getStack()), player.isSneaking() ? tile.wraps.get(h).getStack().getMaxStackSize() : 1, false);
						if (req != null) {
							EntityItem ei = new EntityItem(event.getWorld(), event.getPos().offset(tile.face).getX() + .5, event.getPos().getY() + .3, event.getPos().offset(tile.face).getZ() + .5, req);
							event.getWorld().spawnEntityInWorld(ei);
							Vec3d vec = new Vec3d(player.posX - ei.posX, player.posY + .5 - ei.posY, player.posZ - ei.posZ).normalize().scale(1.5);
							if (ItemHandlerHelper.insertItem(new PlayerMainInvWrapper(player.inventory), ei.getEntityItem(), true) == null) {
								ei.motionX = vec.xCoord;
								ei.motionY = vec.yCoord;
								ei.motionZ = vec.zCoord;
							} else {
								ei.motionX = 0;
								ei.motionY = 0;
								ei.motionZ = 0;
							}
						}
						tile.markForSync();
					}
					event.setCanceled(true);
					event.setResult(Result.DENY);
					event.setUseBlock(Result.DENY);
				}
			}
		}
	}

}
