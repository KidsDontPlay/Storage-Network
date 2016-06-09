package mrriegel.storagenetwork.gui.frequest;

import mrriegel.storagenetwork.gui.AbstractGuiFRequest;
import mrriegel.storagenetwork.tile.TileFRequest;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;

public class GuiFRequest extends AbstractGuiFRequest {
	TileFRequest tile;

	public GuiFRequest(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		tile = ((ContainerFRequest) inventorySlots).tile;
	}

	@Override
	protected boolean getDownwards() {
		return tile.downwards;
	}

	@Override
	protected void setDownwards(boolean d) {
		tile.downwards = d;
	}

	@Override
	protected Sort getSort() {
		return tile.sort;
	}

	@Override
	protected void setSort(Sort s) {
		tile.sort = s;
	}

	@Override
	protected BlockPos getPos() {
		return tile.getPos();
	}

	@Override
	protected BlockPos getMaster() {
		return tile.getMaster();
	}

	@Override
	protected int getDim() {
		return tile.getWorld().provider.getDimension();
	}

}
