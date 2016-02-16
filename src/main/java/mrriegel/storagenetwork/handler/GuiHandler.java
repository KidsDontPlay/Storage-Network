package mrriegel.storagenetwork.handler;

import mrriegel.storagenetwork.gui.cable.ContainerCable;
import mrriegel.storagenetwork.gui.cable.GuiCable;
import mrriegel.storagenetwork.gui.remote.ContainerRemote;
import mrriegel.storagenetwork.gui.remote.GuiRemote;
import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.gui.request.GuiRequest;
import mrriegel.storagenetwork.gui.template.ContainerTemplate;
import mrriegel.storagenetwork.gui.template.GuiTemplate;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int CABLE = 0;
	public static final int REQUEST = 3;
	public static final int REMOTE = 4;
	public static final int TEMPLATE = 5;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == CABLE) {
			return new ContainerCable((TileKabel) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
		}
		if (ID == REQUEST) {
			return new ContainerRequest((TileRequest) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
		}
		if (ID == REMOTE) {
			return new ContainerRemote(player.inventory);
		}
		if (ID == TEMPLATE) {
			return new ContainerTemplate(player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == CABLE) {
			TileKabel tile = (TileKabel) world.getTileEntity(new BlockPos(x, y, z));
			return new GuiCable(new ContainerCable(tile, player.inventory));
		}
		if (ID == REQUEST) {
			return new GuiRequest(new ContainerRequest((TileRequest) world.getTileEntity(new BlockPos(x, y, z)), player.inventory));
		}
		if (ID == REMOTE) {
			return new GuiRemote(new ContainerRemote(player.inventory));
		}
		if (ID == TEMPLATE) {
			return new GuiTemplate(new ContainerTemplate(player.inventory));
		}
		return null;
	}
}
