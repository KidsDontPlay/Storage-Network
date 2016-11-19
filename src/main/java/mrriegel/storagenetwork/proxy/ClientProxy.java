package mrriegel.storagenetwork.proxy;

import java.awt.Color;

import mrriegel.limelib.helper.ColorHelper;
import mrriegel.storagenetwork.ModelCover;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.render.TESRItemMirror;
import mrriegel.storagenetwork.tile.TileItemMirror;
import mrriegel.storagenetwork.tile.TileNetworkToggleCable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(ModelCover.class);

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				if (worldIn != null && pos != null && worldIn.getTileEntity(pos) instanceof TileNetworkToggleCable) {
					return !((TileNetworkToggleCable) worldIn.getTileEntity(pos)).isActive() ? ColorHelper.brighter(Color.red.getRGB(), 0.4) : ColorHelper.brighter(Color.green.getRGB(), 0.5);
				}
				return 0xffffff;
			}
		}, Registry.networkToggleCable);

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				return ColorHelper.brighter(Color.green.getRGB(), 0.5);
			}
		}, Registry.networkToggleCable);
		ClientRegistry.bindTileEntitySpecialRenderer(TileItemMirror.class, new TESRItemMirror());
	}

}
