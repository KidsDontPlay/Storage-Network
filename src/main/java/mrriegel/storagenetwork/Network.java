package mrriegel.storagenetwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.tile.INetworkSaveable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author canitzp
 */
public class Network implements INBTSerializable<NBTTagCompound> {

	public GlobalBlockPos corePosition;
	public List<INetworkSaveable> networkParts = new ArrayList<>();

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		//		NBTTagList list = new NBTTagList();
		for (INetworkSaveable saveable : networkParts) {
			nbt.setTag(String.valueOf(saveable.getPosition()), saveable.serializeNBT());
			//TODO das wird mit globalblockpos nicht mehr funktionieren
			nbt.setString(String.valueOf(saveable.getPosition() + "_class"), saveable.getClass().getName());
			//			list.appendTag(saveable.serializeNBT());
		}
		//		nbt.setTag("list", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		//		NBTTagList list = nbt.getTagList("list", 10);
		//		for (int i = 0; i < list.tagCount(); i++) {
		//			NBTTagCompound n = list.getCompoundTagAt(i);
		//
		//		}

		Set<String> usableKeys = new HashSet<>();
		for (String key : nbt.getKeySet()) {
			if (key.contains("_class")) {
				usableKeys.add(key);
			}
		}
		for (String key : usableKeys) {
			String className = nbt.getString(key);
			String posLong = key.substring(0, key.length() - 6);
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
