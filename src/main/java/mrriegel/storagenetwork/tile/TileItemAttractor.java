package mrriegel.storagenetwork.tile;

import java.util.List;

import com.google.common.collect.Lists;

import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.item.ItemItemFilter;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class TileItemAttractor extends TileNetworkPart implements ITickable {

	public ItemStack filter;

	@Override
	public void update() {
		if (!worldObj.isBlockPowered(pos) && getNetworkCore() != null) {
			int range = 4;
			List<EntityItem> list = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getX() - range, getY() - range, getZ() - range, getX() + range, getY() + range, getZ() + range));
			for (EntityItem ei : list) {
				if (ei.isDead || ei.ticksExisted < 10 || !ItemItemFilter.canTransferItem(filter, ei.getEntityItem()))
					continue;
				Vec3d vec = new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).normalize().scale(0.12);
				if (Math.abs(ei.motionX) < 0.01 && Math.abs(ei.motionZ) < 0.01&&new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).lengthVector() > .9)
					ei.motionY = 0.1;
				ei.motionX = vec.xCoord;
				ei.motionZ = vec.zCoord;
				if (!worldObj.isRemote && new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).lengthVector() < .9) {
					ItemStack stack = ei.getEntityItem().copy();
					ItemStack rest = getNetworkCore().network.insertItem(stack, null, false);
					if (rest == null)
						ei.setDead();
					else
						ei.setEntityItemStack(rest);
				}
			}

		}
	}

	public boolean isDisabled() {
		return worldObj.isBlockPowered(pos);
	}

	@Override
	public List<ItemStack> getDroppingItems() {
		return Lists.newArrayList(filter);
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(StorageNetwork.instance, GuiID.ITEM_ATTRACTOR.ordinal(), worldObj, getX(), getY(), getZ());
		return true;
	}

}
