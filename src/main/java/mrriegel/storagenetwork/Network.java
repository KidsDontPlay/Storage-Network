package mrriegel.storagenetwork;

import mrriegel.storagenetwork.tile.INetworkSaveable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author canitzp
 */
public class Network implements INBTSerializable<NBTTagCompound>{

    public BlockPos corePosition;
    public List<INetworkSaveable> networkParts = new ArrayList<>();

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        for(INetworkSaveable saveable : networkParts){
            nbt.setTag(String.valueOf(saveable.getPosition()), saveable.serializeNBT());
            nbt.setString(String.valueOf(saveable.getPosition() + "_class"), saveable.getClass().getName());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        Set<String> usableKeys = new HashSet<>();
        for(String key : nbt.getKeySet()){
            if(key.contains("_class")){
                usableKeys.add(key);
            }
        }
        for(String key : usableKeys){
            String className = nbt.getString(key);
            String posLong = key.substring(0, key.length()-6);
            try {
                Class clazz = Class.forName(className);
                INetworkSaveable ins = (INetworkSaveable) clazz.newInstance();
                ins.deserializeNBT(nbt.getCompoundTag(posLong));
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
