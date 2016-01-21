package mrriegel.cworks.handler;

import mrriegel.cworks.gui.ContainerImport;
import mrriegel.cworks.gui.GuiImport;
import mrriegel.cworks.tile.TileKabel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int IMPORT = 0;
	public static final int EXPORT = 1;
	public static final int STORAGE = 2;
	public static final int REQUEST = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == IMPORT) {
			return new ContainerImport(
					(TileKabel) world.getTileEntity(new BlockPos(x, y, z)),
					player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == IMPORT) {
			return new GuiImport(new ContainerImport(
					(TileKabel) world.getTileEntity(new BlockPos(x, y, z)),
					player.inventory));
		}
		return null;
	}

}
