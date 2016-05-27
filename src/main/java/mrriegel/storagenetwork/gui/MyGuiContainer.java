package mrriegel.storagenetwork.gui;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;

public abstract class MyGuiContainer extends GuiContainer {

	public MyGuiContainer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	private class AbstractSlot {
		public int x, y, size, guiLeft, guiTop;
		public boolean number, square, smallFont, toolTip;
		protected Minecraft mc;

		public AbstractSlot(int x, int y, int size, int guiLeft, int guiTop, boolean number, boolean square, boolean smallFont, boolean toolTip) {
			super();
			this.x = x;
			this.y = y;
			this.size = size;
			this.guiLeft = guiLeft;
			this.guiTop = guiTop;
			this.number = number;
			this.square = square;
			this.smallFont = smallFont;
			this.toolTip = toolTip;
			mc = Minecraft.getMinecraft();
		}

		public boolean isMouseOverSlot(int mouseX, int mouseY) {
			return isPointInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
		}

	}

	public class ItemSlot extends AbstractSlot {
		public ItemStack stack;

		public ItemSlot(ItemStack stack, int x, int y, int size, int guiLeft, int guiTop, boolean number, boolean square, boolean smallFont, boolean toolTip) {
			super(x, y, size, guiLeft, guiTop, number, square, smallFont, toolTip);
			this.stack = stack;
		}

		public void drawSlot(int mx, int my) {
			GlStateManager.pushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
			String amount = size < 1000 ? String.valueOf(size) : size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
			if (number)
				if (smallFont) {
					GlStateManager.pushMatrix();
					GlStateManager.scale(.5f, .5f, .5f);
					mc.getRenderItem().renderItemOverlayIntoGUI(fontRendererObj, stack, x * 2 + 16, y * 2 + 16, amount);
					GlStateManager.popMatrix();
				} else
					mc.getRenderItem().renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, amount);
			if (square && this.isMouseOverSlot(mx, my)) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = x;
				int k1 = y;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
			GlStateManager.popMatrix();
		}

		public void drawTooltip(int mx, int my) {
			if (toolTip && this.isMouseOverSlot(mx, my) && stack != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
					renderToolTip(stack, mx, my);
				else
					drawHoveringText(Arrays.asList(new String[] { "Amount: " + String.valueOf(size) }), mx, my);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

	}

	public class FluidSlot extends AbstractSlot {
		public Fluid fluid;

		public FluidSlot(Fluid fluid, int x, int y, int size, int guiLeft, int guiTop, boolean number, boolean square, boolean smallFont, boolean toolTip) {
			super(x, y, size, guiLeft, guiTop, number, square, smallFont, toolTip);
			this.fluid = fluid;
		}

		public void drawSlot(int mx, int my) {
			if (fluid != null) {
				GlStateManager.pushMatrix();
				TextureAtlasSprite fluidIcon = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
				if (fluidIcon == null)
					return;
				int color = fluid.getColor(new FluidStack(fluid, 1));
				float a = ((color >> 24) & 0xFF) / 255.0F;
				float r = ((color >> 16) & 0xFF) / 255.0F;
				float g = ((color >> 8) & 0xFF) / 255.0F;
				float b = ((color >> 0) & 0xFF) / 255.0F;
				GlStateManager.color(r, g, b, a);
				this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				drawTexturedModalRect(x, y, fluidIcon, 16, 16);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();
				if (number) {
					String amount = "" + (size < 1000 ? size : size < 1000000 ? size / 1000 : size < 1000000000 ? size / 1000000 : size / 1000000000);
					amount += size < 1000 ? "mB" : size < 1000000 ? "B" : size < 1000000000 ? "KB" : "MB";
					if (smallFont) {
						GlStateManager.pushMatrix();
						GlStateManager.scale(.5f, .5f, .5f);
						mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, new ItemStack(Items.CHAINMAIL_BOOTS), x * 2 + 16, y * 2 + 16, amount);
						GlStateManager.popMatrix();
					} else
						mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, new ItemStack(Items.CHAINMAIL_BOOTS), x, y, amount);
				}
			}
			if (square && this.isMouseOverSlot(mx, my)) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = x;
				int k1 = y;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}

		public void drawTooltip(int mx, int my) {
			if (toolTip && this.isMouseOverSlot(mx, my) && fluid != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || !number) {
					drawHoveringText(Arrays.asList(fluid.getLocalizedName(FluidRegistry.getFluidStack(fluid.getName(), 1))), mx, my, fontRendererObj);
				} else
					drawHoveringText(Arrays.asList(new String[] { "Amount: " + String.valueOf(size) + " mB" }), mx, my);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

	}

}
