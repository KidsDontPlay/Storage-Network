package mrriegel.storagenetwork.gui.crafter;

import mrriegel.storagenetwork.tile.TileCrafter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiCrafter extends GuiContainer {
	private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");
	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");

	public GuiCrafter(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
		TileCrafter tile = ((ContainerCrafter) inventorySlots).crafter;
		int ii = tile.getProgress();
		int jj = tile.getDuration();
		int l = jj != 0 && ii != 0 ? ii * 24 / jj : 0;
		this.drawTexturedModalRect(i + 89, j + 35, 176, 14, l + 1, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(((ContainerCrafter) inventorySlots).crafter.getName(), 8, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		// GlStateManager.pushMatrix();
		// RenderHelper.enableGUIStandardItemLighting();
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glDepthMask(false);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// GlStateManager.color(1, 1, 1, .5F);
		// mc.getRenderItem().renderItemAndEffectIntoGUI(new
		// ItemStack(Blocks.tnt), 4, 79);
		// RenderHelper.disableStandardItemLighting();
		// GlStateManager.popMatrix();
	}

}