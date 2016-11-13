package mrriegel.storagenetwork.tile;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class TileItemAttractor extends TileNetworkPart implements ITickable {

	@Override
	public void update() {
		if (!worldObj.isBlockPowered(pos) && getNetworkCore() != null) {
			int range = 4;
			List<EntityItem> list = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getX() - range, getY() - range, getZ() - range, getX() + range, getY() + range, getZ() + range));
			for (EntityItem ei : list) {
				if (ei.isDead || ei.ticksExisted < 10)
					continue;
				Vec3d vec = new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).normalize().scale(0.12);
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

}
