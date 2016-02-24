package mrriegel.storagenetwork.gui.template;

import java.io.IOException;

import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.TemplateMessage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Mouse;

public class GuiTemplate extends GuiContainer {
	private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");

	public GuiTemplate(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(mc.thePlayer.getHeldItem().getDisplayName(), 8, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		ContainerTemplate con = (ContainerTemplate) inventorySlots;
		for (int i = 1; i < 10; i++) {
			Slot slot = con.getSlot(i);
			if (slot.getHasStack()) {
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				if (NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "ore" + i))
					mc.fontRendererObj.drawStringWithShadow("O", slot.xDisplayPosition + 10, slot.yDisplayPosition, 0x4f94cd);
				if (!NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "meta" + i))
					mc.fontRendererObj.drawStringWithShadow("M", slot.xDisplayPosition + 1, slot.yDisplayPosition, 0xff4040);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (typedChar == 'o' || typedChar == 'm') {
			ContainerTemplate con = (ContainerTemplate) inventorySlots;
			int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
			int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
			for (int i = 1; i < 10; i++) {
				Slot slot = con.getSlot(i);
				if (slot.getHasStack() && isPointInRegion(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
					if (typedChar == 'o' && OreDictionary.getOreIDs(slot.getStack()).length > 0)
						NBTHelper.setBoolean(mc.thePlayer.getHeldItem(), "ore" + i, !NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "ore" + i));
					else if (typedChar == 'm')
						NBTHelper.setBoolean(mc.thePlayer.getHeldItem(), "meta" + i, !NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "meta" + i));
					PacketHandler.INSTANCE.sendToServer(new TemplateMessage(i, NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "ore" + i), NBTHelper.getBoolean(mc.thePlayer.getHeldItem(), "meta" + i)));
					break;
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
}
