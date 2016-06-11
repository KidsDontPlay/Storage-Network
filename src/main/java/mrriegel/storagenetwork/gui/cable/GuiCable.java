package mrriegel.storagenetwork.gui.cable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.gui.MyGuiContainer;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.items.ItemUpgrade;
import mrriegel.storagenetwork.network.ButtonMessage;
import mrriegel.storagenetwork.network.FilterMessage;
import mrriegel.storagenetwork.network.LimitMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.tile.AbstractFilterTile;
import mrriegel.storagenetwork.tile.TileItemBox;
import mrriegel.storagenetwork.tile.TileKabel;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class GuiCable extends MyGuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/cable.png");
	Kind kind;
	Button pPlus, pMinus, white, acti, impor, way;
	AbstractFilterTile tile;
	private GuiTextField searchBar;
	ItemStack stack;
	List<ItemSlot> list = Lists.newArrayList();

	public GuiCable(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.tile = ((ContainerCable) inventorySlots).tile;
		this.ySize = 137;
		if (tile instanceof TileKabel) {
			this.kind = ((TileKabel) tile).getKind();
			stack = ((TileKabel) tile).getStack();
		}
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
		if (tile instanceof TileKabel) {
			if (((TileKabel) tile).isUpgradeable())
				for (int ii = 0; ii < 4; ii++) {
					this.drawTexturedModalRect(i + 97 + ii * 18, j + 5, 176, 34, 18, 18);
				}
			if (((TileKabel) tile).elements(ItemUpgrade.OP) >= 1) {
				this.drawTexturedModalRect(i, j - 26, 0, 137, this.xSize, 30);
				acti.enabled = true;
				acti.visible = true;
			} else {
				acti.enabled = false;
				acti.visible = false;
			}
			if (((TileKabel) tile).elements(ItemUpgrade.OP) >= 1) {
				searchBar.drawTextBox();
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(stack, guiLeft + 8, guiTop - 18);
				RenderHelper.disableStandardItemLighting();
			}
		}
		list = Lists.newArrayList();
		for (int ii = 0; ii < 9; ii++) {
			ItemStack s = tile.getFilter().get(ii) == null ? null : tile.getFilter().get(ii).getStack();
			int num = tile.getFilter().get(ii) == null ? 0 : tile.getFilter().get(ii).getSize();
			boolean numShow = tile instanceof TileKabel ? ((TileKabel) tile).elements(3) > 0 : false;
			list.add(new ItemSlot(s, guiLeft + 8 + ii * 18, guiTop + 26, num, guiLeft, guiTop, numShow, true, false, true));
		}
		for (ItemSlot s : list)
			s.drawSlot(mouseX, mouseY);
		if (tile instanceof TileKabel) {
			if (((TileKabel) tile).elements(ItemUpgrade.OP) >= 1 && mouseX > guiLeft + 7 && mouseX < guiLeft + 25 && mouseY > guiTop + -19 && mouseY < guiTop + -1) {
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
		}
		fontRendererObj.drawString(String.valueOf(tile.getPriority()), guiLeft + 34 - fontRendererObj.getStringWidth(String.valueOf(tile.getPriority())) / 2, guiTop + 10, 4210752);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0; i < list.size(); i++) {
			ItemSlot e = list.get(i);
			ContainerCable con = (ContainerCable) inventorySlots;
			if (e.stack != null) {
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				if (con.tile.getOres().get(i) != null && con.tile.getOres().get(i))
					mc.fontRendererObj.drawStringWithShadow("O", e.x + 10, e.y, 0x4f94cd);
				if (con.tile.getMetas().get(i) == null || !con.tile.getMetas().get(i))
					mc.fontRendererObj.drawStringWithShadow("M", e.x + 1, e.y, 0xff4040);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
		int mx = Mouse.getX() * this.width / this.mc.displayWidth;
		int my = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (mx > guiLeft + 29 && mx < guiLeft + 37 && my > guiTop + 10 && my < guiTop + 20) {
			List<String> list = Lists.newArrayList();
			list.add("Priority");
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			this.drawHoveringText(list, mx, my, fontRendererObj);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}

		if (white != null && white.isMouseOver()) {
			List<String> list = Lists.newArrayList();
			list.add(tile.isWhite() ? "Whitelist" : "Blacklist");
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			this.drawHoveringText(list, mx, my, fontRendererObj);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for (ItemSlot s : list)
			s.drawTooltip(mouseX, mouseY);
		if (impor.isMouseOver())
			drawHoveringText(Lists.newArrayList("Import Filter"), mouseX - guiLeft, mouseY - guiTop);
		if (way.isMouseOver())
			drawHoveringText(Lists.newArrayList(I18n.format("gui.storagenetwork.fil.tooltip_" + tile.getWay().toString())), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	public void initGui() {
		super.initGui();
		pMinus = new Button(0, guiLeft + 7, guiTop + 5, "-");
		buttonList.add(pMinus);
		pPlus = new Button(1, guiLeft + 45, guiTop + 5, "+");
		buttonList.add(pPlus);
		if (tile.isStorage()) {
			impor = new Button(5, guiLeft + 95, guiTop + 5, "I");
			buttonList.add(impor);
			way = new Button(6, guiLeft + 115, guiTop + 5, "");
			buttonList.add(way);
		}

		if (tile instanceof TileItemBox || kind == Kind.imKabel || kind == Kind.storageKabel) {
			white = new Button(3, guiLeft + 70, guiTop + 5, "");
			buttonList.add(white);
		}
		if (tile instanceof TileKabel) {
			Keyboard.enableRepeatEvents(true);
			searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 36, guiTop - 14, 85, fontRendererObj.FONT_HEIGHT);
			searchBar.setMaxStringLength(30);
			searchBar.setEnableBackgroundDrawing(false);
			searchBar.setVisible(true);
			searchBar.setTextColor(16777215);
			searchBar.setCanLoseFocus(false);
			searchBar.setFocused(true);
			searchBar.setText(((TileKabel) tile).getLimit() + "");
			acti = new Button(4, guiLeft + 127, guiTop - 18, "");
			buttonList.add(acti);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (tile instanceof TileKabel && ((TileKabel) tile).elements(ItemUpgrade.OP) >= 1 && mouseX > guiLeft + 7 && mouseX < guiLeft + 25 && mouseY > guiTop + -19 && mouseY < guiTop + -1) {
			stack = mc.thePlayer.inventory.getItemStack();
			((TileKabel) tile).setStack(stack);
			int num = searchBar.getText().isEmpty() ? 0 : Integer.valueOf(searchBar.getText());
			PacketHandler.INSTANCE.sendToServer(new LimitMessage(num, tile.getPos(), stack));
		} else
			super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (int i = 0; i < list.size(); i++) {
			ItemSlot e = list.get(i);
			if (e.isMouseOverSlot(mouseX, mouseY)) {
				ContainerCable con = (ContainerCable) inventorySlots;
				StackWrapper x = con.tile.getFilter().get(i);
				if (mc.thePlayer.inventory.getItemStack() != null) {
					if (!con.in(new StackWrapper(mc.thePlayer.inventory.getItemStack(), 1))) {
						con.tile.getFilter().put(i, new StackWrapper(mc.thePlayer.inventory.getItemStack(), mc.thePlayer.inventory.getItemStack().stackSize));
						con.tile.getOres().put(i, false);
						con.tile.getMetas().put(i, true);
					}
				} else {
					if (x != null) {
						if (mouseButton == 0)
							x.setSize(x.getSize() + (isShiftKeyDown() ? 10 : 1));
						else if (mouseButton == 1)
							x.setSize(x.getSize() - (isShiftKeyDown() ? 10 : 1));
						else if (mouseButton == 2) {
							con.tile.getFilter().put(i, null);
							con.tile.getOres().put(i, false);
							con.tile.getMetas().put(i, true);
						}
						if (x != null && x.getSize() <= 0) {
							con.tile.getFilter().put(i, null);
							con.tile.getOres().put(i, false);
							con.tile.getMetas().put(i, true);
						}
					}
				}
				con.slotChanged();
				PacketHandler.INSTANCE.sendToServer(new FilterMessage(i, tile.getFilter().get(i), tile.getOre(i), tile.getMeta(i)));
				break;
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		PacketHandler.INSTANCE.sendToServer(new ButtonMessage(button.id, tile.getPos()));
		switch (button.id) {
		case 0:
			tile.setPriority(tile.getPriority() - 1);
			break;
		case 1:
			tile.setPriority(tile.getPriority() + 1);
			break;
		case 3:
			tile.setWhite(!tile.isWhite());
			break;
		case 4:
			if (tile instanceof TileKabel)
				((TileKabel) tile).setMode(!((TileKabel) tile).isMode());
			break;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (typedChar == 'o' || typedChar == 'm') {
			for (int i = 0; i < list.size(); i++) {
				ItemSlot e = list.get(i);
				int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
				int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
				ContainerCable con = (ContainerCable) inventorySlots;
				if (e.isMouseOverSlot(mouseX, mouseY) && e.stack != null) {
					if (typedChar == 'o' && OreDictionary.getOreIDs(e.stack).length > 0)
						con.tile.getOres().put(i, !con.tile.getOres().get(i));
					else if (typedChar == 'm')
						con.tile.getMetas().put(i, !con.tile.getMetas().get(i));
					con.slotChanged();
					PacketHandler.INSTANCE.sendToServer(new FilterMessage(i, tile.getFilter().get(i), tile.getOre(i), tile.getMeta(i)));
					break;
				}
			}
		}
		if (!(tile instanceof TileKabel)) {
			super.keyTyped(typedChar, keyCode);
			return;
		}
		if (!this.checkHotbarKeys(keyCode)) {
			Keyboard.enableRepeatEvents(true);
			String s = "";
			if (((TileKabel) tile).elements(ItemUpgrade.OP) >= 1) {
				s = searchBar.getText();
			}
			if ((((TileKabel) tile).elements(ItemUpgrade.OP) >= 1) && this.searchBar.textboxKeyTyped(typedChar, keyCode)) {
				if (!StringUtils.isNumeric(searchBar.getText()) && !searchBar.getText().isEmpty())
					searchBar.setText(s);
				int num = 0;
				try {
					num = searchBar.getText().isEmpty() ? 0 : Integer.valueOf(searchBar.getText());
				} catch (Exception e) {
					searchBar.setText("0");
				}
				((TileKabel) tile).setLimit(num);
				PacketHandler.INSTANCE.sendToServer(new LimitMessage(num, tile.getPos(), stack));
			} else {
				super.keyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
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
				if (id == 3) {
					if (tile.isWhite())
						this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 3, 176, 83, 13, 10);
					else
						this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 3, 190, 83, 13, 10);

				}
				if (id == 4) {
					if (((TileKabel) tile).isMode())
						this.drawTexturedModalRect(this.xPosition + 0, this.yPosition + 0, 176, 94, 16, 15);
					else
						this.drawTexturedModalRect(this.xPosition + 0, this.yPosition + 0, 176 + 16, 94, 16, 15);

				}
				if (id == 6) {
					this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, 176 + tile.getWay().ordinal() * 12, 114, 12, 12);
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
				if (tile instanceof TileKabel) {
					List<String> lis = new ArrayList<String>();
					String s = I18n.format("gui.storagenetwork.operate.tooltip", mc.theWorld.getBlockState(tile.getPos()).getBlock().getLocalizedName(), I18n.format("gui.storagenetwork.operate.tooltip." + (((TileKabel) tile).isMode() ? "more" : "less")), ((TileKabel) tile).getLimit(), ((TileKabel) tile).getStack() != null ? ((TileKabel) tile).getStack().getDisplayName() : "Items");
					List<String> matchList = new ArrayList<String>();
					Pattern regex = Pattern.compile(".{1,25}(?:\\s|$)", Pattern.DOTALL);
					Matcher regexMatcher = regex.matcher(s);
					while (regexMatcher.find()) {
						matchList.add(regexMatcher.group());
					}
					lis = new ArrayList<String>(matchList);
					if (this.hovered && id == 4 && ((TileKabel) tile).getStack() != null) {
						GlStateManager.pushMatrix();
						GlStateManager.disableLighting();
						drawHoveringText(lis, p_146112_2_, p_146112_3_, fontRendererObj);
						GlStateManager.enableLighting();
						GlStateManager.popMatrix();
					}
				}
				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}
	}

}
