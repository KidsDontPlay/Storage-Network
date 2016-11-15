package mrriegel.storagenetwork.message;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.AbstractMessage;
import mrriegel.storagenetwork.gui.GuiNetworkCore;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkEnergyCell;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MessageCoreSync extends AbstractMessage<MessageCoreSync> {

	public MessageCoreSync() {
	}

	public MessageCoreSync(TileNetworkCore core) {
		nbt.setInteger("nsize", core.network.networkParts.size());
		int cell = 0, maxcell = 0;
		for (INetworkPart part : core.network.noCables)
			if (part instanceof TileNetworkEnergyCell) {
				cell += ((TileNetworkEnergyCell) part).getEnergy().getEnergyStored();
				maxcell += ((TileNetworkEnergyCell) part).getEnergy().getMaxEnergyStored();
			}
		nbt.setInteger("cell", cell);
		nbt.setInteger("maxcell", maxcell);
		Map<String, Integer> map = Maps.newHashMap();
		for (INetworkPart p : core.network.networkParts) {
			String block = ((TileEntity) p).getBlockType().getLocalizedName();
			map.put(block, map.get(block) != null ? (map.get(block) + 1) : 1);

		}
		List<Entry<String, Integer>> lis = Lists.newArrayList();
		for (Entry<String, Integer> e : map.entrySet()) {
			lis.add(e);
		}
		Collections.sort(lis, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});
		List<String> strings = Lists.newArrayList();
		for (Entry<String, Integer> e : lis)
			strings.add("  " + e.getKey() + ": " + e.getValue());
		NBTHelper.setStringList(nbt, "parts", strings);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiNetworkCore) {
			GuiNetworkCore gui = (GuiNetworkCore) Minecraft.getMinecraft().currentScreen;
			gui.data = nbt;
		}
	}

}
