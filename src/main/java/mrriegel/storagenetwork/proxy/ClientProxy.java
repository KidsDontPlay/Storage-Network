package mrriegel.storagenetwork.proxy;

import java.awt.Color;

import mrriegel.limelib.helper.ColorHelper;
import mrriegel.storagenetwork.ModelCover;
import mrriegel.storagenetwork.Registry;
import mrriegel.storagenetwork.block.BlockItemMirror;
import mrriegel.storagenetwork.render.TESRItemMirror;
import mrriegel.storagenetwork.tile.TileItemMirror;
import mrriegel.storagenetwork.tile.TileNetworkToggleCable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(ModelCover.class);
		MinecraftForge.EVENT_BUS.register(ClientProxy.class);

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

	@SubscribeEvent
	public static void render(Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.objectMouseOver.getBlockPos() == null || mc.theWorld.getTileEntity(mc.objectMouseOver.getBlockPos()) == null)
			return;
		TileEntity t = mc.theWorld.getTileEntity(mc.objectMouseOver.getBlockPos());
		if (event.getType() == ElementType.TEXT && t instanceof TileItemMirror/*&&mc.thePlayer.isSneaking()*/) {
			ScaledResolution sr = event.getResolution();
			TileItemMirror tile = (TileItemMirror) t;
			if (mc.objectMouseOver.sideHit == tile.face) {
				float f1 = (float) (mc.objectMouseOver.hitVec.xCoord - (double) mc.objectMouseOver.getBlockPos().getX());
				float f2 = (float) (mc.objectMouseOver.hitVec.yCoord - (double) mc.objectMouseOver.getBlockPos().getY());
				float f3 = (float) (mc.objectMouseOver.hitVec.zCoord - (double) mc.objectMouseOver.getBlockPos().getZ());
				int h = BlockItemMirror.getQuadrant(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()), f1, f2, f3);
				if (h >= 0 && h <= 3 && tile.wraps.get(h) != null) {
					String text = /*tile.wraps.get(h).getStack().getRarity().rarityColor +*/(tile.wraps.get(h).getSize() + " ") + tile.wraps.get(h).getStack().getDisplayName();
					mc.fontRendererObj.drawString(text, (sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(text)) / 2, (sr.getScaledHeight() - 15 - mc.fontRendererObj.FONT_HEIGHT) / 2, ColorHelper.getRGB(0xffff00, mc.thePlayer.isSneaking()?255:50), true);
				}
			}
		}
	}

}
