package mrriegel.storagenetwork.gui.indicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.gui.cable.ContainerCable;
import mrriegel.storagenetwork.gui.cable.GuiCable;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.network.ButtonMessage;
import mrriegel.storagenetwork.network.FilterMessage;
import mrriegel.storagenetwork.network.LimitMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.tile.TileIndicator;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiIndicator extends GuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/cable.png");
	Button acti;
	TileIndicator tile;
	Slot slot;

	public GuiIndicator(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.tile = ((ContainerIndicator) inventorySlots).tile;
		this.ySize = 137;

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {String s=tile.getWorld().getBlockState(tile.getPos()).getBlock().getLocalizedName();
	this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		int ii = 4;
		this.drawTexturedModalRect(i + 7 + ii * 18, j + 25, 176, 34, 18, 18);
		// this.drawTexturedModalRect(i + 150, j + 6, 176, 110, 16, 16);
		if (tile.getStack() != null) {
			slot.stack = tile.getStack().getStack();
			slot.size = tile.getStack().getSize();
		}
		slot.drawSlot(mouseX, mouseY);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		int mx = Mouse.getX() * this.width / this.mc.displayWidth;
		int my = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		slot.drawTooltip(mouseX, mouseY);

	}

	@Override
	public void initGui() {
		super.initGui();
		acti = new Button(4, guiLeft + 127, guiTop + 26, "");
		buttonList.add(acti);
		slot = new Slot(tile.getStack(), guiLeft + 8 + 4 * 18, guiTop + 26, guiLeft, guiTop);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (slot.isMouseOverSlot(mouseX, mouseY)) {
			ContainerIndicator con = (ContainerIndicator) inventorySlots;
			StackWrapper x = con.getFilter();
			if (mc.thePlayer.inventory.getItemStack() != null) {
				con.setFilter(new StackWrapper(mc.thePlayer.inventory.getItemStack(), mc.thePlayer.inventory.getItemStack().stackSize));
			} else {
				if (x != null) {
					if (mouseButton == 0)
						x.setSize(x.getSize() + (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 10 : 1));
					else if (mouseButton == 1)
						x.setSize(x.getSize() - (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 10 : 1));
					else if (mouseButton == 2) {
						x = null;
					}
					if (x != null && x.getSize() <= 0)
						x = null;
					con.setFilter(x);
				}
			}
			con.slotChanged();
			slot = new Slot(tile.getStack(), guiLeft + 8 + 4 * 18, guiTop + 26, guiLeft, guiTop);
			PacketHandler.INSTANCE.sendToServer(new FilterMessage(0, con.getFilter(), false, false));
		}

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		PacketHandler.INSTANCE.sendToServer(new ButtonMessage(button.id, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
		tile.setMore(!tile.isMore());
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	class Slot {
		ItemStack stack;
		int x, y, size, guiLeft, guiTop;
		Minecraft mc = Minecraft.getMinecraft();

		public Slot(StackWrapper stack, int x, int y, int guiLeft, int guiTop) {
			this.stack = stack == null ? null : stack.getStack();
			this.x = x;
			this.y = y;
			this.size = stack == null ? 0 : stack.getSize();
			this.guiLeft = guiLeft;
			this.guiTop = guiTop;
		}

		void drawSlot(int mx, int my) {
			if (stack != null) {
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
				String amount = size < 1000 ? String.valueOf(size) : size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
				mc.getRenderItem().renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, amount);
			}
			if (this.isMouseOverSlot(mx, my)) {
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

		void drawTooltip(int mx, int my) {
			if (stack != null && this.isMouseOverSlot(mx, my)) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				renderToolTip(stack, mx, my);
				else
					drawHoveringText(Arrays.asList(size+""), mx, my);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		boolean isMouseOverSlot(int mouseX, int mouseY) {
			return isPointInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
		}
	}

	class Button extends GuiButton {

		public Button(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, String p_i1021_6_) {
			super(p_i1021_1_, p_i1021_2_, p_i1021_3_, 16, 16, p_i1021_6_);
		}

		@Override
		public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
			if (this.visible) {
				FontRenderer fontrenderer = p_146112_1_.fontRendererObj;
				p_146112_1_.getTextureManager().bindTexture(texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
				int k = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 160 + 16 * k, 52, 16, 16);
				// if (id == 2) {
				// this.drawTexturedModalRect(this.xPosition + 4, this.yPosition
				// + 4, 180, 70, 8, 9);
				// if (!tile.isMeta())
				// this.drawTexturedModalRect(this.xPosition + 2, this.yPosition
				// + 2, 195, 70, 12, 12);
				// }
				if (tile.isMore())
					this.drawTexturedModalRect(this.xPosition + 0, this.yPosition + 0, 176, 94, 16, 15);
				else
					this.drawTexturedModalRect(this.xPosition + 0, this.yPosition + 0, 176 + 16, 94, 16, 15);

				this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
				int l = 14737632;

				if (packedFGColour != 0) {
					l = packedFGColour;
				} else if (!this.enabled) {
					l = 10526880;
				} else if (this.hovered) {
					l = 16777120;
				}
				List<String> lis = new ArrayList<String>();
				String s = tile.getStack() == null ? "" : StatCollector.translateToLocalFormatted("gui.storagenetwork.indi.tooltip", mc.theWorld.getBlockState(tile.getPos()).getBlock().getLocalizedName(), StatCollector.translateToLocal("gui.storagenetwork.operate.tooltip." + (tile.isMore() ? "more" : "less")), tile.getStack().getSize(), tile.getStack() != null ? tile.getStack().getStack().getDisplayName() : "Items");
				List<String> matchList = new ArrayList<String>();
				Pattern regex = Pattern.compile(".{1,25}(?:\\s|$)", Pattern.DOTALL);
				Matcher regexMatcher = regex.matcher(s);
				while (regexMatcher.find()) {
					matchList.add(regexMatcher.group());
				}
				lis = new ArrayList<String>(matchList);
				if (this.hovered && id == 4 && tile.getStack() != null) {
					GlStateManager.pushMatrix();
					GlStateManager.disableLighting();
					drawHoveringText(lis, p_146112_2_, p_146112_3_, fontRendererObj);
					GlStateManager.enableLighting();
					GlStateManager.popMatrix();
				}
				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}
	}

}
