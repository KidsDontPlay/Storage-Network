package mrriegel.storagenetwork.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mrriegel.storagenetwork.CreativeTab;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.api.IConnectable;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.helper.Inv;
import mrriegel.storagenetwork.tile.TileMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class BlockMaster extends BlockContainer {

	public BlockMaster() {
		super(Material.iron);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTab.tab1);
		this.setUnlocalizedName(StorageNetwork.MODID + ":master");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMaster();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		boolean hasMaster = false;
		for (BlockPos p : TileMaster.getSides(pos)) {
			if (worldIn.getTileEntity(p) instanceof IConnectable
					&& ((IConnectable) worldIn.getTileEntity(p)).getMaster() != null) {
				hasMaster = true;
				break;
			}
		}
		if (hasMaster) {
			worldIn.setBlockToAir(pos);
			Block.spawnAsEntity(worldIn, pos, Inv.copyStack(stack, 1));
		} else {
			if (worldIn.getTileEntity(pos) != null)
				((TileMaster) worldIn.getTileEntity(pos)).refreshNetwork();
		}
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		TileMaster tile = (TileMaster) worldIn.getTileEntity(pos);
		tile.refreshNetwork();
		ItemStack s = playerIn.getHeldItem();
		if (s != null && FluidContainerRegistry.isContainer(s)) {
			int space = tile.tank.getCapacity() - tile.tank.getFluidAmount();
			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(s);
			if (liquid != null && liquid.getFluid().equals(FluidRegistry.LAVA)
					&& space >= liquid.amount) {
				ItemStack container = s.getItem().getContainerItem(s);
				tile.tank.fill(liquid, true);
				if (!playerIn.capabilities.isCreativeMode) {
					s.stackSize--;
					if (s.stackSize <= 0)
						playerIn.inventory.setInventorySlotContents(
								playerIn.inventory.currentItem, null);
					playerIn.inventory.addItemStackToInventory(container);
					playerIn.openContainer.detectAndSendChanges();
				}

			}
			return true;
		}
		if (!worldIn.isRemote) {
			if (ConfigHandler.lavaNeeded)
				playerIn.addChatMessage(new ChatComponentText("Lava: "
						+ tile.tank.getFluidAmount() + "/"
						+ tile.tank.getCapacity()));
			playerIn.addChatMessage(new ChatComponentText("Connectables: "
					+ tile.connectables.size()));
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (BlockPos p : tile.connectables) {
				String block = worldIn.getBlockState(p).getBlock()
						.getLocalizedName();
				map.put(block, map.get(block) != null ? (map.get(block) + 1)
						: 1);

			}
			List<Entry<String, Integer>> lis = new ArrayList<Map.Entry<String, Integer>>();
			for (Entry<String, Integer> e : map.entrySet()) {
				lis.add(e);
			}
			Collections.sort(lis, new Comparator<Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> o1,
						Entry<String, Integer> o2) {
					return Integer.compare(o2.getValue(), o1.getValue());
				}
			});
			for (Entry<String, Integer> e : lis)
				playerIn.addChatMessage(new ChatComponentText("   "
						+ e.getKey() + ": " + e.getValue()));
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side,
				hitX, hitY, hitZ);
	}

}
