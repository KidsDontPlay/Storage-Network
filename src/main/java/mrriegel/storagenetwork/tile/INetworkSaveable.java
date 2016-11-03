package mrriegel.storagenetwork.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author canitzp
 */
//TODO add specific methods
public interface INetworkSaveable extends INBTSerializable<NBTTagCompound>{

    BlockPos getPosition();

}
