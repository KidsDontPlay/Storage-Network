package mrriegel.cworks.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mrriegel.cworks.blocks.BlockKabel;
import mrriegel.cworks.blocks.BlockMaster;
import mrriegel.cworks.init.ModBlocks;
import mrriegel.cworks.tile.TileKabel.Kind;
import net.minecraft.block.BlockGlass;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileMaster extends TileEntity implements ITickable {
	Set<BlockPos> cables, storageInventorys, imInventorys, exInventorys;
	boolean active;

	private void addCables(BlockPos pos, int num) {
		if (cables == null)
			cables = new HashSet<BlockPos>();
		if (num >= 500) {
			System.out.println("too much cables");
			active = false;
			return;
		}
		for (BlockPos bl : getSides(pos)) {
			if (worldObj.getBlockState(bl).getBlock() == ModBlocks.master
					&& !bl.equals(pos)) {
				active = false;
				return;
			}
			if (worldObj.getBlockState(bl).getBlock() instanceof BlockKabel
					&& !cables.contains(bl)
					&& worldObj.getChunkFromBlockCoords(bl).isLoaded()) {
				cables.add(bl);
				((TileKabel) worldObj.getTileEntity(bl)).setMaster(this.pos);
				addCables(bl, num++);
			}
		}
		active = true;
	}

	private void addInventorys() {
		storageInventorys = new HashSet<BlockPos>();
		imInventorys = new HashSet<BlockPos>();
		exInventorys = new HashSet<BlockPos>();
		for (BlockPos cable : cables) {
			TileKabel tile = (TileKabel) worldObj.getTileEntity(cable);
			if (tile.getKind() == Kind.exKabel) {
				for (EnumFacing face : tile.getConnections()) {
					if (worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
							&& worldObj.getChunkFromBlockCoords(
									cable.offset(face)).isLoaded())
						exInventorys.add(cable.offset(face));
				}
			} else if (tile.getKind() == Kind.imKabel) {
				for (EnumFacing face : tile.getConnections()) {
					if (worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
							&& worldObj.getChunkFromBlockCoords(
									cable.offset(face)).isLoaded())
						imInventorys.add(cable.offset(face));
				}
			} else if (tile.getKind() == Kind.storageKabel) {
				for (EnumFacing face : tile.getConnections()) {
					if (worldObj.getTileEntity(cable.offset(face)) instanceof IInventory
							&& worldObj.getChunkFromBlockCoords(
									cable.offset(face)).isLoaded())
						storageInventorys.add(cable.offset(face));
				}
			}
		}
	}

	public static List<BlockPos> getSides(BlockPos pos) {
		List<BlockPos> lis = new ArrayList<BlockPos>();
		lis.add(pos.up());
		lis.add(pos.down());
		lis.add(pos.east());
		lis.add(pos.west());
		lis.add(pos.north());
		lis.add(pos.south());
		return lis;
	}

	public void refreshNetwork() {
		cables = null;
		addCables(pos, 0);
		addInventorys();
		System.out.println("ex: "+exInventorys);
		System.out.println("im: "+imInventorys);
		System.out.println("stor: "+storageInventorys);
		if (!active)
			return;
	}

	public void vacuum() {
		if (worldObj.getTotalWorldTime() % 20 != 0)
			return;
		for (BlockPos p : cables) {
			if (((TileKabel) worldObj.getTileEntity(p)).getKind() == Kind.vacuumKabel) {
				int range = 6;

				int x = getPos().getX();
				int y = getPos().getY();
				int z = getPos().getZ();

				List<EntityItem> items = worldObj.getEntitiesWithinAABB(
						EntityItem.class,
						AxisAlignedBB.fromBounds(x - range, y - range, z
								- range, x + range + 1, y + range + 1, z
								+ range + 1));

				for (EntityItem item : items) {
					if (item.getAge() < 60 || item.getAge() >= 105
							&& item.getAge() < 110 || item.isDead)
						continue;
					ItemStack stack = item.getEntityItem();

				}
			}
		}
	}

	@Override
	public void update() {
	}

}
