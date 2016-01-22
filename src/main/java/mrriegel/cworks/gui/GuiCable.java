package mrriegel.cworks.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mrriegel.cworks.CableWorks;
import mrriegel.cworks.network.ButtonMessage;
import mrriegel.cworks.network.PacketHandler;
import mrriegel.cworks.tile.TileKabel;
import mrriegel.cworks.tile.TileKabel.Kind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiCable extends GuiContainer {
	private ResourceLocation texture = new ResourceLocation(CableWorks.MODID
			+ ":textures/gui/cable.png");
	Kind kind;
	Button pPlus, pMinus, meta, white;
	TileKabel tile;

	public GuiCable(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 137;
		this.tile = ((ContainerCable) inventorySlots).tile;
		this.kind = tile.getKind();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,
			int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		for (int ii = 0; ii < 9; ii++) {
			this.drawTexturedModalRect(i + 7 + ii * 18, j + 25, 176, 34, 18, 18);
		}
		fontRendererObj.drawString(
				String.valueOf(tile.getPriority()),
				guiLeft
						+ 34
						- fontRendererObj.getStringWidth(String.valueOf(tile
								.getPriority())) / 2, guiTop + 10, 4210752);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		int mx = Mouse.getX() * this.width / this.mc.displayWidth;
		int my = this.height - Mouse.getY() * this.height
				/ this.mc.displayHeight - 1;
		if (mx > guiLeft + 27 && mx < guiLeft + 35 && my > guiTop + 10
				&& my < guiTop + 20) {
			List<String> list = new ArrayList<String>();
			list.add("Priority");
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			this.drawHoveringText(list, mx, my, fontRendererObj);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		pMinus = new Button(0, guiLeft + 7, guiTop + 5, "-");
		buttonList.add(pMinus);
		pPlus = new Button(1, guiLeft + 45, guiTop + 5, "+");
		buttonList.add(pPlus);
		meta = new Button(2, guiLeft + 78, guiTop + 5, "");
		buttonList.add(meta);
		if (kind == Kind.imKabel || kind == Kind.storageKabel) {
			white = new Button(3, guiLeft + 110, guiTop + 5, "");
			buttonList.add(white);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		PacketHandler.INSTANCE.sendToServer(new ButtonMessage(button.id, tile
				.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
		switch (button.id) {
		case 0:
			tile.setPriority(tile.getPriority() - 1);
			break;
		case 1:
			tile.setPriority(tile.getPriority() + 1);
			break;
		case 2:
			tile.setMeta(!tile.isMeta());
			break;
		case 3:
			tile.setWhite(!tile.isWhite());
			break;
		}
	}

	class Button extends GuiButton {

		public Button(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_,
				String p_i1021_6_) {
			super(p_i1021_1_, p_i1021_2_, p_i1021_3_, 16, 16, p_i1021_6_);
		}

		@Override
		public void drawButton(Minecraft p_146112_1_, int p_146112_2_,
				int p_146112_3_) {
			if (this.visible) {
				FontRenderer fontrenderer = p_146112_1_.fontRendererObj;
				p_146112_1_.getTextureManager().bindTexture(texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = p_146112_2_ >= this.xPosition
						&& p_146112_3_ >= this.yPosition
						&& p_146112_2_ < this.xPosition + this.width
						&& p_146112_3_ < this.yPosition + this.height;
				int k = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition,
						160 + 16 * k, 52, 16, 16);
				if (id == 2) {
					this.drawTexturedModalRect(this.xPosition + 4,
							this.yPosition + 4, 180, 70, 8, 9);
					if (!tile.isMeta())
						this.drawTexturedModalRect(this.xPosition + 2,
								this.yPosition + 2, 195, 70, 12, 12);
				}
				if (id == 3) {
					if (tile.isWhite())
						this.drawTexturedModalRect(this.xPosition + 1,
								this.yPosition + 3, 176, 83, 13, 10);
					else
						this.drawTexturedModalRect(this.xPosition + 1,
								this.yPosition + 3, 190, 83, 13, 10);

				}
				this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
				int l = 14737632;

				if (packedFGColour != 0) {
					l = packedFGColour;
				} else if (!this.enabled) {
					l = 10526880;
				} else if (this.hovered) {
					l = 16777120;
				}

				this.drawCenteredString(fontrenderer, this.displayString,
						this.xPosition + this.width / 2, this.yPosition
								+ (this.height - 8) / 2, l);
			}
		}
	}

}
