package mrriegel.cworks.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mrriegel.cworks.CableWorks;
import mrriegel.cworks.helper.StackWrapper;
import mrriegel.cworks.network.PacketHandler;
import mrriegel.cworks.network.RequestMessage;
import mrriegel.cworks.tile.TileMaster;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

public class GuiRequest extends GuiContainer {
	private ResourceLocation texture = new ResourceLocation(CableWorks.MODID
			+ ":textures/gui/request.png");
	int page, maxPage;
	public List<StackWrapper> stacks;
	ItemStack over;

	public GuiRequest(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 256;
		stacks = ((TileMaster) ((ContainerRequest) inventorySlotsIn).tile
				.getWorld().getTileEntity(
						((ContainerRequest) inventorySlotsIn).tile.getMaster()))
				.getStacks();
		Collections.sort(stacks, new Comparator<StackWrapper>() {
			@Override
			public int compare(StackWrapper o1, StackWrapper o2) {
				return Integer.compare(o1.getSize(), o2.getSize());
			}
		});
		BlockPos master = ((ContainerRequest) inventorySlots).tile.getMaster();
		PacketHandler.INSTANCE.sendToServer(new RequestMessage(0,
				master.getX(), master.getY(), master.getZ(), null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,
			int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		over = null;
		for (int ii = 0; ii < Math.min(8, stacks.size()); ii++) {
			new Slot(stacks.get(ii).getStack(), guiLeft + 10 + ii * 20,
					guiTop + 10, stacks.get(ii).getSize()).drawSlot(mouseX,
					mouseY);
		}
		for (int ii = 0; ii < Math.min(8, stacks.size()); ii++) {
			new Slot(stacks.get(ii).getStack(), guiLeft + 10 + ii * 20,
					guiTop + 10, stacks.get(ii).getSize()).drawTooltip(mouseX,
					mouseY);
		}

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (over != null && (mouseButton == 0 || mouseButton == 1)) {
			BlockPos master = ((ContainerRequest) inventorySlots).tile
					.getMaster();
			PacketHandler.INSTANCE.sendToServer(new RequestMessage(mouseButton,
					master.getX(), master.getY(), master.getZ(), over));
		}
	}

	class Slot {
		ItemStack stack;
		int x, y, size;

		public Slot(ItemStack stack, int x, int y, int size) {
			super();
			this.stack = stack;
			this.x = x;
			this.y = y;
			this.size = size;
		}

		void drawSlot(int mx, int my) {
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
			mc.getRenderItem().renderItemOverlayIntoGUI(
					fontRendererObj,
					stack,
					x,
					y,
					size < 1000 ? String.valueOf(size)
							: size < 1000000 ? String.valueOf(size).substring(
									0, 1)
									+ "K" : String.valueOf(size)
									.substring(0, 1) + "M");
			if (this.isMouseOverSlot(mx, my)) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = x;
				int k1 = y;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433,
						-2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				over = stack;
			}
		}

		void drawTooltip(int mx, int my) {
			if (this.isMouseOverSlot(mx, my)) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				renderToolTip(stack, mx, my);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		private boolean isMouseOverSlot(int mouseX, int mouseY) {
			return isPointInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX,
					mouseY);
		}
	}

}
