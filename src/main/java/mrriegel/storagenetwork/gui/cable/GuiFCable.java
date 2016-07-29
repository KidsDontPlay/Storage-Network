package mrriegel.storagenetwork.gui.cable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.gui.MyGuiContainer;
import mrriegel.storagenetwork.helper.StackWrapper;
import mrriegel.storagenetwork.helper.Util;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class GuiFCable extends MyGuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/cable.png");
	Kind kind;
	Button pPlus, pMinus, white, acti, way;
	AbstractFilterTile tile;
	private GuiTextField searchBar;
	List<FluidSlot> list;
	FluidSlot operation;

	public GuiFCable(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 171;
		this.tile = ((ContainerFCable) inventorySlots).tile;
		if (tile instanceof TileKabel) {
			this.kind = ((TileKabel) tile).getKind();
		}
		list = Lists.newArrayList();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		for (int ii = 0; ii < 9; ii++) {
			for (int jj = 0; jj < 2; jj++)
				this.drawTexturedModalRect(i + 7 + ii * 18, j + 25 + 18 * jj, 176, 34, 18, 18);
		}
		if (tile instanceof TileKabel) {
			if (((TileKabel) tile).isUpgradeable())
				for (int ii = 0; ii < 4; ii++) {
					this.drawTexturedModalRect(i + 97 + ii * 18, j + 5, 176, 34, 18, 18);
				}
			if (((TileKabel) tile).elements(ItemUpgrade.OP) >= 1) {
				acti.enabled = true;
				acti.visible = true;
				this.mc.getTextureManager().bindTexture(texture);
				this.drawTexturedModalRect(i + 7, j + 65, 176, 34, 18, 18);
				this.drawTexturedModalRect(i + 30, j + 67, 0, 171, 90, 12);
				searchBar.drawTextBox();
			} else {
				acti.enabled = false;
				acti.visible = false;
			}
		}
		list = Lists.newArrayList();
		for (int jj = 0; jj < 2; jj++) {
			for (int ii = 0; ii < 9; ii++) {
				int index = ii + (9 * jj);
				StackWrapper wrap = tile.getFilter().get(index);
				ItemStack s = wrap == null ? null : wrap.getStack();
				int num = wrap == null ? 0 : wrap.getSize();
				Fluid f = Util.getFluid(s) == null ? null : Util.getFluid(s).getFluid();
				list.add(new FluidSlot(f, guiLeft + 8 + ii * 18, guiTop + 26 + jj * 18, num, guiLeft, guiTop, false, true, false, true));
			}
		}
		for (FluidSlot s : list)
			s.drawSlot(mouseX, mouseY);
		if (tile instanceof TileKabel && ((TileKabel) tile).elements(ItemUpgrade.OP) >= 1)
			operation.drawSlot(mouseX, mouseY);
		fontRendererObj.drawString(String.valueOf(tile.getPriority()), guiLeft + 34 - fontRendererObj.getStringWidth(String.valueOf(tile.getPriority())) / 2, guiTop + 10, 4210752);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for (FluidSlot s : list)
			s.drawTooltip(mouseX, mouseY);
		if (tile instanceof TileKabel && ((TileKabel) tile).elements(ItemUpgrade.OP) >= 1)
			operation.drawTooltip(mouseX, mouseY);
		if (way != null && way.isMouseOver())
			drawHoveringText(Lists.newArrayList(I18n.format("gui.storagenetwork.fil.tooltip_" + tile.getWay().toString())), mouseX - guiLeft, mouseY - guiTop);
		if (mouseX > guiLeft + 29 && mouseX < guiLeft + 37 && mouseY > guiTop + 10 && mouseY < guiTop + 20)
			this.drawHoveringText(Lists.newArrayList("Priority"), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
		if (white != null && white.isMouseOver())
			this.drawHoveringText(Lists.newArrayList(tile.isWhite() ? "Whitelist" : "Blacklist"), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
	}

	@Override
	public void initGui() {
		super.initGui();
		pMinus = new Button(0, guiLeft + 7, guiTop + 5, "-");
		buttonList.add(pMinus);
		pPlus = new Button(1, guiLeft + 45, guiTop + 5, "+");
		buttonList.add(pPlus);
		if (tile.isStorage()) {
			way = new Button(6, guiLeft + 115, guiTop + 5, "");
			buttonList.add(way);
		}

		if (tile instanceof TileItemBox || kind == Kind.imKabel || kind == Kind.storageKabel) {
			white = new Button(3, guiLeft + 70, guiTop + 5, "");
			buttonList.add(white);
		}
		if (tile instanceof TileKabel) {
			Keyboard.enableRepeatEvents(true);
			searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 34, guiTop + 69, 85, fontRendererObj.FONT_HEIGHT);
			searchBar.setMaxStringLength(30);
			searchBar.setEnableBackgroundDrawing(false);
			searchBar.setVisible(true);
			searchBar.setTextColor(16777215);
			searchBar.setCanLoseFocus(false);
			searchBar.setFocused(true);
			searchBar.setText(((TileKabel) tile).getLimit() + "");
			acti = new Button(4, guiLeft + 127, guiTop + 65, "");
			buttonList.add(acti);
			Fluid f = Util.getFluid(((TileKabel) tile).getStack()) == null ? null : Util.getFluid(((TileKabel) tile).getStack()).getFluid();
			operation = new FluidSlot(f, guiLeft + 8, guiTop + 66, 1, guiLeft, guiTop, false, true, false, true);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (operation.isMouseOverSlot(mouseX, mouseY) && ((TileKabel) tile).elements(ItemUpgrade.OP) >= 1) {
			((TileKabel) tile).setStack(mc.thePlayer.inventory.getItemStack());
			operation.fluid = Util.getFluid(mc.thePlayer.inventory.getItemStack()) == null ? null : Util.getFluid(mc.thePlayer.inventory.getItemStack()).getFluid();
			int num = searchBar.getText().isEmpty() ? 0 : Integer.valueOf(searchBar.getText());
			PacketHandler.INSTANCE.sendToServer(new LimitMessage(num, tile.getPos(), mc.thePlayer.inventory.getItemStack()));
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			FluidSlot e = list.get(i);
			if (e.isMouseOverSlot(mouseX, mouseY)) {
				ContainerFCable con = (ContainerFCable) inventorySlots;
				StackWrapper x = con.tile.getFilter().get(i);
				if (mc.thePlayer.inventory.getItemStack() != null && Util.getFluid(mc.thePlayer.inventory.getItemStack()) != null) {
					if (!con.in(new StackWrapper(mc.thePlayer.inventory.getItemStack(), 1))) {
						con.tile.getFilter().put(i, new StackWrapper(mc.thePlayer.inventory.getItemStack(), 1));
					} else
						con.tile.getFilter().put(i, null);
				} else {
					con.tile.getFilter().put(i, null);
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
				PacketHandler.INSTANCE.sendToServer(new LimitMessage(num, tile.getPos(), ((TileKabel) tile).getStack()));
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
					if (((TileKabel) tile).getStack() != null) {
						List<String> lis = new ArrayList<String>();
						String s = I18n.format("gui.storagenetwork.operate.tooltip", mc.theWorld.getBlockState(tile.getPos()).getBlock().getLocalizedName(), I18n.format("gui.storagenetwork.operate.tooltip." + (((TileKabel) tile).isMode() ? "more" : "less")), ((TileKabel) tile).getLimit() + " mB", Util.getFluid(((TileKabel) tile).getStack()).getFluid().getName());
						List<String> matchList = new ArrayList<String>();
						Pattern regex = Pattern.compile(".{1,25}(?:\\s|$)", Pattern.DOTALL);
						Matcher regexMatcher = regex.matcher(s);
						while (regexMatcher.find()) {
							matchList.add(regexMatcher.group());
						}
						lis = new ArrayList<String>(matchList);
						if (this.hovered && id == 4) {
							GlStateManager.pushMatrix();
							GlStateManager.disableLighting();
							drawHoveringText(lis, p_146112_2_, p_146112_3_, fontRendererObj);
							GlStateManager.enableLighting();
							GlStateManager.popMatrix();
						}
					}
				}
				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}
	}

}
