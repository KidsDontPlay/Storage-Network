package mrriegel.storagenetwork;

import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.tile.INetworkPart;
import mrriegel.storagenetwork.tile.TileNetworkCore;

import com.google.common.collect.Sets;

/**
 * @author canitzp
 */
public class Network {

	public GlobalBlockPos corePosition;
	public Set<INetworkPart> networkParts = Sets.newHashSet();

	public void addPart(INetworkPart part) {
		networkParts.add(part);
		part.setNetworkCore((TileNetworkCore) corePosition.getTile(null));
	}

	public void removePart(INetworkPart part) {
		networkParts.remove(part);
		part.setNetworkCore(null);
	}

	@Override
	public String toString() {
		return "Network at '" + corePosition.toString() + "'. Data: {" + networkParts.toString() + "}";
	}

	//item start

	public ItemStack requestItem(FilterItem fil, final int size, boolean simulate) {
		if (size == 0 || fil == null)
			return null;
		return null;
	}

	public int insertItem(ItemStack stack, BlockPos source, boolean simulate) {
		return 0;
	}

	public void exportItems() {

	}

	public void importItems() {

	}

	//item end

	//fluid start

	public ItemStack requestFluid(FluidStack fluid, final int size, boolean simulate) {
		if (size == 0 || fluid == null)
			return null;
		return null;
	}

	public int insertFluid(FluidStack fluid, BlockPos source, boolean simulate) {
		return 0;
	}

	public void exportFluids() {

	}

	public void importFluids() {

	}

	//fluid end

}
