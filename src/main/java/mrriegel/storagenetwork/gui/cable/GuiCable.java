package mrriegel.storagenetwork.gui.cable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.init.ModItems;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.network.ButtonMessage;
import mrriegel.storagenetwork.network.FilterMessage;
import mrriegel.storagenetwork.network.LimitMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiCable extends GuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/cable.png");
	Kind kind;
	Button pPlus, pMinus, meta, white, acti;
	TileKabel tile;
	private GuiTextField searchBar;
	ItemStack stack;
	ArrayList<Slot> list = new ArrayList<GuiCable.Slot>();

	public GuiCable(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.tile = ((ContainerCable) inventorySlots).tile;
		this.ySize = 137;
		this.kind = tile.getKind();
		stack = tile.getStack();

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
		if (tile.elements(ItemUpgrade.OP) >= 1)
			this.drawTexturedModalRect(i, j - 26, 0, 137, this.xSize, 30);
		this.drawTexturedModalRect(i + 150, j + 6, 176, 110, 16, 16);
		if (tile.elements(ItemUpgrade.OP) >= 1) {
			searchBar.drawTextBox();
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemAndEffectIntoGUI(stack, guiLeft + 8, guiTop - 18);
			RenderHelper.disableStandardItemLighting();
		}
		list = new ArrayList<GuiCable.Slot>();
		for (int ii = 0; ii < 9; ii++) {
			ItemStack s = tile.getFilter().get(ii) == null ? null : tile.getFilter().get(ii).getStack();
			int num = tile.getFilter().get(ii) == null ? 0 : tile.getFilter().get(ii).getSize();
			list.add(new Slot(s, guiLeft + 8 + ii * 18, guiTop + 26, num));
		}
		for (Slot s : list)
			s.drawSlot(mouseX, mouseY);
		if (tile.elements(ItemUpgrade.OP) >= 1 && mouseX > guiLeft + 7 && mouseX < guiLeft + 25 && mouseY > guiTop + -19 && mouseY < guiTop + -1) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int j1 = guiLeft + 8;
			int k1 = guiTop - 18;
			GlStateManager.colorMask(true, true, true, false);
			drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}
		fontRendererObj.drawString(String.valueOf(tile.getPriority()), guiLeft + 34 - fontRendererObj.getStringWidth(String.valueOf(tile.getPriority())) / 2, guiTop + 10, 4210752);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		int mx = Mouse.getX() * this.width / this.mc.displayWidth;
		int my = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (mx > guiLeft + 29 && mx < guiLeft + 37 && my > guiTop + 10 && my < guiTop + 20) {
			List<String> list = new ArrayList<String>();
			list.add("Priority");
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			this.drawHoveringText(list, mx, my, fontRendererObj);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}

		for (Slot s : list)
			s.drawTooltip(mouseX, mouseY);
		int i = mouseX;
		int j = mouseY;
		if (i > guiLeft + 150 && i < guiLeft + 166 && j > guiTop + 6 && j < guiTop + 22) {
			List<String> list = new ArrayList<String>();
			for (int ii = 0; ii < ItemUpgrade.NUM; ii++)
				list.add(tile.elements(ii) + "x " + new ItemStack(ModItems.upgrade, 1, ii).getDisplayName());
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			this.drawHoveringText(list, i, j, fontRendererObj);
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
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
		if (tile.elements(ItemUpgrade.OP) >= 1) {
			Keyboard.enableRepeatEvents(true);
			searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 36, guiTop - 14, 85, fontRendererObj.FONT_HEIGHT);
			searchBar.setMaxStringLength(30);
			searchBar.setEnableBackgroundDrawing(false);
			searchBar.setVisible(true);
			searchBar.setTextColor(16777215);
			searchBar.setCanLoseFocus(false);
			searchBar.setFocused(true);
			searchBar.setText(tile.getLimit() + "");
			acti = new Button(4, guiLeft + 127, guiTop - 18, "");
			buttonList.add(acti);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (tile.elements(ItemUpgrade.OP) >= 1 && mouseX > guiLeft + 7 && mouseX < guiLeft + 25 && mouseY > guiTop + -19 && mouseY < guiTop + -1) {
			stack = mc.thePlayer.inventory.getItemStack();
			tile.setStack(stack);
			int num = searchBar.getText().isEmpty() ? 0 : Integer.valueOf(searchBar.getText());
			PacketHandler.INSTANCE.sendToServer(new LimitMessage(num, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), stack));
		} else
			super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (int i = 0; i < 9; i++) {
			Slot e = list.get(i);
			if (e.isMouseOverSlot(mouseX, mouseY)) {
				ContainerCable con = (ContainerCable) inventorySlots;
				StackWrapper x = con.getFilter().get(i);
				if (mc.thePlayer.inventory.getItemStack() != null) {
					if (!((ContainerCable) inventorySlots).in(new StackWrapper(mc.thePlayer.inventory.getItemStack(), 1)))
						con.getFilter().put(i, new StackWrapper(mc.thePlayer.inventory.getItemStack(), mc.thePlayer.inventory.getItemStack().stackSize));
				} else {
					if (x != null) {
						if (mouseButton == 0)
							x.setSize(x.getSize() + (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 10 : 1));
						else if (mouseButton == 1)
							x.setSize(x.getSize() - (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 10 : 1));
						else if (mouseButton == 2)
							con.getFilter().put(i, null);
						if (x != null && x.getSize() <= 0)
							con.getFilter().put(i, null);
					}
				}
				((ContainerCable) inventorySlots).slotChanged();
				PacketHandler.INSTANCE.sendToServer(new FilterMessage(i, tile.getFilter().get(i)));
				break;
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		PacketHandler.INSTANCE.sendToServer(new ButtonMessage(button.id, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
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
		case 4:
			tile.setMode(!tile.isMode());
			break;
		}
	}

	@Override
	protected void keyTyped(char c, int p_73869_2_) throws IOException {
		if (!this.checkHotbarKeys(p_73869_2_)) {
			Keyboard.enableRepeatEvents(true);
			String s = "";
			if (tile.elements(ItemUpgrade.OP) >= 1) {
				s = searchBar.getText();
			}
			if ((tile.elements(ItemUpgrade.OP) >= 1) && this.searchBar.textboxKeyTyped(c, p_73869_2_)) {
				if (!StringUtils.isNumeric(searchBar.getText()) && !searchBar.getText().isEmpty())
					searchBar.setText(s);
				int num = 0;
				try {
					num = searchBar.getText().isEmpty() ? 0 : Integer.valueOf(searchBar.getText());
				} catch (Exception e) {
					searchBar.setText("0");
				}
				tile.setLimit(num);
				PacketHandler.INSTANCE.sendToServer(new LimitMessage(num, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), stack));
			} else {
				super.keyTyped(c, p_73869_2_);
			}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	class Slot {
		ItemStack stack;
		int x, y, size;

		public Slot(ItemStack stack, int x, int y, int size) {
			this.stack = stack;
			this.x = x;
			this.y = y;
			this.size = size;
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
				renderToolTip(stack, mx, my);
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
				if (id == 2) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 4, 180, 70, 8, 9);
					if (!tile.isMeta())
						this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, 195, 70, 12, 12);
				}
				if (id == 3) {
					if (tile.isWhite())
						this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 3, 176, 83, 13, 10);
					else
						this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 3, 190, 83, 13, 10);

				}
				if (id == 4) {
					if (tile.isMode())
						this.drawTexturedModalRect(this.xPosition + 0, this.yPosition + 0, 176, 94, 16, 15);
					else
						this.drawTexturedModalRect(this.xPosition + 0, this.yPosition + 0, 176 + 16, 94, 16, 15);

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
				List<String> lis = new ArrayList<String>();
				String s = StatCollector.translateToLocalFormatted("gui.storagenetwork.operate.tooltip", mc.theWorld.getBlockState(tile.getPos()).getBlock().getLocalizedName(), StatCollector.translateToLocal("gui.storagenetwork.operate.tooltip." + (tile.isMode() ? "more" : "less")), tile.getLimit(), tile.getStack() != null ? tile.getStack().getDisplayName() : "Items");
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
