package mrriegel.storagenetwork.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/** by Pahimar */

public class NBTHelper {
	public static boolean hasTag(NBTTagCompound nbt, String keyName) {
		return nbt != null && nbt.hasKey(keyName);
	}

	public static void removeTag(NBTTagCompound nbt, String keyName) {
		if (nbt != null) {
			nbt.removeTag(keyName);
		}
	}

	private static void initNBTTagCompound(NBTTagCompound nbt) {
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
	}

	// list
	public static NBTTagList getList(NBTTagCompound nbt, String tag, int objtype, boolean nullifyOnFail) {
		initNBTTagCompound(nbt);
		return hasTag(nbt, tag) ? nbt.getTagList(tag, objtype) : nullifyOnFail ? null : new NBTTagList();
	}

	public static void setList(NBTTagCompound nbt, String tag, NBTTagList list) {
		initNBTTagCompound(nbt);
		nbt.setTag(tag, list);
	}

	public static NBTTagList createList(NBTBase... list) {
		NBTTagList tagList = new NBTTagList();
		for (NBTBase b : list)
			tagList.appendTag(b);
		return tagList;
	}

	public static NBTTagList createList(List<NBTBase> list) {
		NBTTagList tagList = new NBTTagList();
		for (NBTBase b : list)
			tagList.appendTag(b);
		return tagList;
	}

	// String
	public static String getString(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			return null;
		}

		return nbt.getString(keyName);
	}

	public static void setString(NBTTagCompound nbt, String keyName, String keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null)
			nbt.setString(keyName, keyValue);
	}

	// boolean
	public static boolean getBoolean(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setBoolean(nbt, keyName, false);
		}

		return nbt.getBoolean(keyName);
	}

	public static void setBoolean(NBTTagCompound nbt, String keyName, boolean keyValue) {
		initNBTTagCompound(nbt);

		nbt.setBoolean(keyName, keyValue);
	}

	// byte
	public static byte getByte(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setByte(nbt, keyName, (byte) 0);
		}

		return nbt.getByte(keyName);
	}

	public static void setByte(NBTTagCompound nbt, String keyName, byte keyValue) {
		initNBTTagCompound(nbt);

		nbt.setByte(keyName, keyValue);
	}

	// short
	public static short getShort(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setShort(nbt, keyName, (short) 0);
		}

		return nbt.getShort(keyName);
	}

	public static void setShort(NBTTagCompound nbt, String keyName, short keyValue) {
		initNBTTagCompound(nbt);

		nbt.setShort(keyName, keyValue);
	}

	// int
	public static int getInteger(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setInteger(nbt, keyName, 0);
		}

		return nbt.getInteger(keyName);
	}

	public static void setInteger(NBTTagCompound nbt, String keyName, int keyValue) {
		initNBTTagCompound(nbt);

		nbt.setInteger(keyName, keyValue);
	}

	// long
	public static long getLong(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setLong(nbt, keyName, 0);
		}

		return nbt.getLong(keyName);
	}

	public static void setLong(NBTTagCompound nbt, String keyName, long keyValue) {
		initNBTTagCompound(nbt);

		nbt.setLong(keyName, keyValue);
	}

	// float
	public static float getFloat(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setFloat(nbt, keyName, 0);
		}

		return nbt.getFloat(keyName);
	}

	public static void setFloat(NBTTagCompound nbt, String keyName, float keyValue) {
		initNBTTagCompound(nbt);

		nbt.setFloat(keyName, keyValue);
	}

	// double
	public static double getDouble(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setDouble(nbt, keyName, 0);
		}

		return nbt.getDouble(keyName);
	}

	public static void setDouble(NBTTagCompound nbt, String keyName, double keyValue) {
		initNBTTagCompound(nbt);

		nbt.setDouble(keyName, keyValue);
	}

	// itemstack
	public static ItemStack getItemStack(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			setItemStack(nbt, keyName, null);
		}
		NBTTagCompound res = (NBTTagCompound) nbt.getTag(keyName);
		return ItemStack.loadItemStackFromNBT(res);

	}

	public static void setItemStack(NBTTagCompound nbt, String keyName, ItemStack keyValue) {
		initNBTTagCompound(nbt);
		NBTTagCompound res = new NBTTagCompound();
		if (keyValue != null) {
			keyValue.writeToNBT(res);
		}

		nbt.setTag(keyName, res);
	}

	// enum
	public static <E extends Enum<E>> E getEnum(NBTTagCompound nbt, String keyName, Class<E> clazz) {
		initNBTTagCompound(nbt);

		if (!nbt.hasKey(keyName)) {
			return null;
		}
		String s = nbt.getString(keyName);
		for (E e : clazz.getEnumConstants()) {
			if (e.toString().equals(s))
				return e;
		}
		return null;

	}

	public static <E extends Enum<E>> void setEnum(NBTTagCompound nbt, String keyName, E keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null)
			nbt.setString(keyName, keyValue.toString());
	}

	// Stringlist
	public static List<String> getStringList(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		ArrayList<String> lis = new ArrayList<String>();
		int size = getInteger(nbt, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getString(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setStringList(NBTTagCompound nbt, String keyName, List<String> keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null) {
			setInteger(nbt, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				String s = keyValue.get(i);
				if (s != null)
					setString(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Booleanlist
	public static List<Boolean> getBooleanList(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		ArrayList<Boolean> lis = new ArrayList<Boolean>();
		int size = getInteger(nbt, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getBoolean(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setBooleanList(NBTTagCompound nbt, String keyName, List<Boolean> keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null) {
			setInteger(nbt, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Boolean s = keyValue.get(i);
				if (s != null)
					setBoolean(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Integerlist
	public static List<Integer> getIntegerList(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		ArrayList<Integer> lis = new ArrayList<Integer>();
		int size = getInteger(nbt, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getInteger(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setIntegerList(NBTTagCompound nbt, String keyName, List<Integer> keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null) {
			setInteger(nbt, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Integer s = keyValue.get(i);
				if (s != null)
					setInteger(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Doublelist
	public static List<Double> getDoubleList(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		ArrayList<Double> lis = new ArrayList<Double>();
		int size = getInteger(nbt, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getDouble(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setDoubleList(NBTTagCompound nbt, String keyName, List<Double> keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null) {
			setInteger(nbt, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Double s = keyValue.get(i);
				if (s != null)
					setDouble(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Stacklist
	public static List<ItemStack> getItemStackList(NBTTagCompound nbt, String keyName) {
		initNBTTagCompound(nbt);

		ArrayList<ItemStack> lis = new ArrayList<ItemStack>();
		int size = getInteger(nbt, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getItemStack(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setItemStackList(NBTTagCompound nbt, String keyName, List<ItemStack> keyValue) {
		initNBTTagCompound(nbt);
		if (keyValue != null) {
			setInteger(nbt, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				ItemStack s = keyValue.get(i);
				if (s != null)
					setItemStack(nbt, keyName + ":" + i, s);
			}
		}
	}

}
