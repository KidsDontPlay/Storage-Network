package mrriegel.storagenetwork.gui.container;

import java.io.IOException;
import java.util.Arrays;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.network.FaceMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.tile.TileContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiContainer extends net.minecraft.client.gui.inventory.GuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/cable.png");
	Button in, out;
	TileContainer tile;

	public GuiContainer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.tile = ((ContainerContainer) inventorySlots).tile;
		this.ySize = 137;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		for (int ii = 0; ii < 9; ii++) {
			this.drawTexturedModalRect(i + 7 + ii * 18, j + 25, 176, 34, 18, 18);
		}
		in.displayString = tile.getInput().toString().substring(0, 1).toUpperCase();
		out.displayString = tile.getOutput().toString().substring(0, 1).toUpperCase();

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ContainerContainer con = (ContainerContainer) inventorySlots;
		for (Slot slot : con.inventorySlots) {
			if (slot.getHasStack() && slot.getStack().isItemEqual(new ItemStack(ModItems.template)) && slot.getStack().getTagCompound() != null) {
				RenderHelper.disableStandardItemLighting();
				GlStateManager.pushMatrix();
				GlStateManager.disableDepth();
				float scale = 0.6f;
				float pos = 1f / scale;
				GlStateManager.scale(scale, scale, scale);
				RenderHelper.enableGUIStandardItemLighting();
				NBTTagCompound res = (NBTTagCompound) slot.getStack().getTagCompound().getTag("res");
				mc.getRenderItem().zLevel += 500;
				if (res != null)
					mc.getRenderItem().renderItemAndEffectIntoGUI(ItemStack.loadItemStackFromNBT(res), (int) (slot.xDisplayPosition * pos + 11), (int) (slot.yDisplayPosition * pos));
				mc.getRenderItem().zLevel -= 500;
				RenderHelper.disableStandardItemLighting();
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();
			}
		}
		for (GuiButton b : buttonList)
			if (b.isMouseOver()) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				drawHoveringText(Arrays.asList(b.id == 0 ? "Input" : "Output"), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
	}

	@Override
	public void initGui() {
		super.initGui();
		in = new Button(0, guiLeft + 7, guiTop + 5, "-");
		buttonList.add(in);
		out = new Button(1, guiLeft + 45, guiTop + 5, "+");
		buttonList.add(out);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		PacketHandler.INSTANCE.sendToServer(new FaceMessage(button.id, tile.getPos()));
		switch (button.id) {
		case 0:
			tile.setInput(next(tile.getInput()));
			break;
		case 1:
			tile.setOutput(next(tile.getOutput()));
			break;
		}
	}

	public static EnumFacing next(EnumFacing f) {
		EnumFacing[] vals = EnumFacing.values();
		return vals[(f.ordinal() + 1) % vals.length];
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
				this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
				int l = 14737632;

				if (packedFGColour != 0) {
					l = packedFGColour;
				} else if (!this.enabled) {
					l = 10526880;
				} else if (this.hovered) {
					l = 16777120;
				}

				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}

	}
}
