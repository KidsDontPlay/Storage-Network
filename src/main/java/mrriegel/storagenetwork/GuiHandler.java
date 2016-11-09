package mrriegel.storagenetwork;

import mrriegel.storagenetwork.gui.GuiNetworkCore;
import mrriegel.storagenetwork.tile.TileNetworkCore;
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
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiID.values()[ID]) {
		case NETWORK_CORE:
			return new GuiNetworkCore((TileNetworkCore) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	public enum GuiID {
		NETWORK_CORE;
	}

}
