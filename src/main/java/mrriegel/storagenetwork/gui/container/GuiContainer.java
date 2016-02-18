package mrriegel.storagenetwork.gui.container;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.tile.TileContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiContainer extends net.minecraft.client.gui.inventory.GuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/cable.png");
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
			this.drawTexturedModalRect(i + 7 + ii * 18, j + 19, 176, 34, 18, 18);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ContainerContainer con = (ContainerContainer) inventorySlots;
		for (Slot slot : con.inventorySlots) {
			if (slot.getHasStack() && slot.getStack().isItemEqual(new ItemStack(ModItems.template))) {
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
	}

}
