package mrriegel.storagenetwork;

import mrriegel.storagenetwork.container.ContainerItemConnect;
import mrriegel.storagenetwork.container.ContainerItemFilter;
import mrriegel.storagenetwork.container.ContainerRequestTable;
import mrriegel.storagenetwork.gui.GuiEnergyInterface;
import mrriegel.storagenetwork.gui.GuiItemConnect;
import mrriegel.storagenetwork.gui.GuiItemFilter;
import mrriegel.storagenetwork.gui.GuiNetworkCore;
import mrriegel.storagenetwork.gui.GuiRequestTable;
import mrriegel.storagenetwork.tile.TileNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkEnergyInterface;
import mrriegel.storagenetwork.tile.TileNetworkItemConnection;
import mrriegel.storagenetwork.tile.TileRequestTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiID.values()[ID]) {
		case NETWORK_CORE:
			return null;
		case ITEM_FILTER:
			return new ContainerItemFilter(player.inventory, player.getHeldItemMainhand());
		case ENERGY_INTERFACE:
			return null;
		case ITEM_CONNECTOR:
			return new ContainerItemConnect(player.inventory, (TileNetworkItemConnection) world.getTileEntity(new BlockPos(x, y, z)));
		case REQUEST_TABLE:
			return new ContainerRequestTable(player.inventory,(TileRequestTable) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiID.values()[ID]) {
		case NETWORK_CORE:
			return new GuiNetworkCore((TileNetworkCore) world.getTileEntity(new BlockPos(x, y, z)));
		case ITEM_FILTER:
			return new GuiItemFilter(new ContainerItemFilter(player.inventory, player.getHeldItemMainhand()));
		case ENERGY_INTERFACE:
			return new GuiEnergyInterface((TileNetworkEnergyInterface) world.getTileEntity(new BlockPos(x, y, z)));
		case ITEM_CONNECTOR:
			return new GuiItemConnect(new ContainerItemConnect(player.inventory, (TileNetworkItemConnection) world.getTileEntity(new BlockPos(x, y, z))));
		case REQUEST_TABLE:
			return new GuiRequestTable(new ContainerRequestTable(player.inventory,(TileRequestTable) world.getTileEntity(new BlockPos(x, y, z))));
		default:
			return null;
		}
	}

	public enum GuiID {
		NETWORK_CORE, ITEM_FILTER, ENERGY_INTERFACE, ITEM_CONNECTOR, REQUEST_TABLE;
	}

}
