package mrriegel.cworks.handler;

import mrriegel.cworks.gui.ContainerCable;
import mrriegel.cworks.gui.ContainerRequest;
import mrriegel.cworks.gui.GuiCable;
import mrriegel.cworks.gui.GuiRequest;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int CABLE = 0;
	public static final int REQUEST = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == CABLE) {
			return new ContainerCable(
					(TileKabel) world.getTileEntity(new BlockPos(x, y, z)),
					player.inventory);
		}
		if (ID == REQUEST) {
			return new ContainerRequest(
					(TileRequest) world.getTileEntity(new BlockPos(x, y, z)),
					player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == CABLE) {
			TileKabel tile = (TileKabel) world.getTileEntity(new BlockPos(x, y,
					z));
			return new GuiCable(new ContainerCable(tile, player.inventory));
		}
		if(ID==REQUEST){
			return new GuiRequest(new ContainerRequest(
					(TileRequest) world.getTileEntity(new BlockPos(x, y, z)),
					player.inventory));
		}
		return null;
	}
}
