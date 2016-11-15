package mrriegel.storagenetwork.container;

import java.util.List;

import mrriegel.storagenetwork.Enums.Sort;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerRequestTable extends ContainerAbstractRequest<TileRequestTable> {

	public ContainerRequestTable(InventoryPlayer invPlayer, TileRequestTable tile) {
		super(invPlayer, tile);
	}

	@Override
	public List<ItemStack> getMatrixList() {
		return object.matrix;
	}

	@Override
	public TileNetworkCore getNetworkCore() {
		return object.getNetworkCore();
	}

	@Override
	protected void saveMatrix() {
		object.matrix.clear();
		for (int i = 0; i < 9; i++)
			object.matrix.add(getMatrix().getStackInSlot(i));
		object.markDirty();
	}

	@Override
	public Sort getSort() {
		return object.sort;
	}

	@Override
	public boolean isTopdown() {
		return object.topDown;
	}

	@Override
	public boolean isJEI() {
		return object.jei;
	}

}
