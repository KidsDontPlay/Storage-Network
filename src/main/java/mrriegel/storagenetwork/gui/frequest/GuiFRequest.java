package mrriegel.storagenetwork.gui.frequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.helper.Util;
import mrriegel.storagenetwork.network.FRequestMessage;
import mrriegel.storagenetwork.network.PacketHandler;
import mrriegel.storagenetwork.network.SortMessage;
import mrriegel.storagenetwork.tile.TileFRequest;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiFRequest extends GuiContainer {
	private ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/frequest.png");
	int page = 1, maxPage = 1;
	public List<FluidStack> fluids;
	Fluid over;
	private GuiTextField searchBar;
	private Button direction, sort, left, right;
	TileFRequest tile;
	BlockPos master;

	public GuiFRequest(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 256;
		this.fluids = new ArrayList<FluidStack>();
		tile = ((ContainerFRequest) inventorySlots).tile;
		master = tile.getMaster();
		PacketHandler.INSTANCE.sendToServer(new FRequestMessage(0, master.getX(), master.getY(), master.getZ(), null));
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		searchBar = new GuiTextField(0, fontRendererObj, guiLeft + 81, guiTop + 96 + 64, 85, fontRendererObj.FONT_HEIGHT);
		searchBar.setMaxStringLength(30);
		searchBar.setEnableBackgroundDrawing(false);
		searchBar.setVisible(true);
		searchBar.setTextColor(16777215);
		searchBar.setCanLoseFocus(false);
		searchBar.setFocused(true);
		direction = new Button(0, guiLeft + 7, guiTop + 93 + 64, "");
		buttonList.add(direction);
		sort = new Button(1, guiLeft + 21, guiTop + 93 + 64, "");
		buttonList.add(sort);
		left = new Button(2, guiLeft + 44, guiTop + 93 + 64, "<");
		buttonList.add(left);
		right = new Button(3, guiLeft + 58, guiTop + 93 + 64, ">");
		buttonList.add(right);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		over = null;
		String search = searchBar.getText();
		List<FluidStack> tmp = search.equals("") ? new ArrayList<FluidStack>(fluids) : new ArrayList<FluidStack>();
		if (!search.equals("")) {
			for (FluidStack s : fluids)
				if (s.getLocalizedName().toLowerCase().contains(search.toLowerCase()))
					tmp.add(s);
		}
		Collections.sort(tmp, new Comparator<FluidStack>() {
			int mul = tile.downwards ? -1 : 1;

			@Override
			public int compare(FluidStack o2, FluidStack o1) {
				switch (tile.sort) {
				case AMOUNT:
					return Integer.compare(o1.amount, o2.amount) * mul;
				case NAME:
					return o2.getLocalizedName().compareToIgnoreCase(o1.getLocalizedName()) * mul;
				case MOD:
					return Util.getModNameForFluid(o2.getFluid()).compareToIgnoreCase(Util.getModNameForFluid(o1.getFluid())) * mul;
				}
				return 0;
			}
		});
		maxPage = tmp.size() / 32;
		if (tmp.size() % 32 != 0)
			maxPage++;
		if (maxPage < 1)
			maxPage = 1;
		if (page < 1)
			page = 1;
		if (page > maxPage)
			page = maxPage;
		if (page == 1) {
			left.visible = false;
			left.enabled = false;
		} else {
			left.visible = true;
			left.enabled = true;
		}
		if (page == maxPage) {
			right.visible = false;
			right.enabled = false;
		} else {
			right.visible = true;
			right.enabled = true;
		}
		searchBar.drawTextBox();
		int index = (page - 1) * 32;
		for (int jj = 0; jj < 4; jj++) {
			for (int ii = 0; ii < 8; ii++) {
				int in = index;
				if (in >= tmp.size())
					break;
				new Slot(tmp.get(in).getFluid(), guiLeft + 10 + ii * 20, guiTop + 10 + jj * 20, tmp.get(in).amount, guiLeft, guiTop).drawSlot(mouseX, mouseY);
				index++;
			}
		}
		index = (page - 1) * 32;
		for (int jj = 0; jj < 4; jj++) {
			for (int ii = 0; ii < 8; ii++) {
				int in = index;
				if (in >= tmp.size())
					break;
				new Slot(tmp.get(in).getFluid(), guiLeft + 10 + ii * 20, guiTop + 10 + jj * 20, tmp.get(in).amount, guiLeft, guiTop).drawTooltip(mouseX, mouseY);
				index++;
			}
		}

	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 2 && page > 1)
			page--;
		if (button.id == 3 && page < maxPage)
			page++;
		if (button.id == 0)
			tile.downwards = !tile.downwards;
		else if (button.id == 1)
			tile.sort = tile.sort.next();
		PacketHandler.INSTANCE.sendToServer(new SortMessage(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), tile.downwards, tile.sort));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int i = Mouse.getX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (i > (guiLeft + 81) && i < (guiLeft + xSize - 7) && j > (guiTop + 96) && j < (guiTop + 103) && mouseButton == 1) {
			searchBar.setText("");
		} else if (over != null && (mouseButton == 0 || mouseButton == 1) && mc.thePlayer.inventory.getItemStack() == null) {
			PacketHandler.INSTANCE.sendToServer(new FRequestMessage(mouseButton, master.getX(), master.getY(), master.getZ(), over));
		}
	}

	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException {
		if (!this.checkHotbarKeys(p_73869_2_)) {
			Keyboard.enableRepeatEvents(true);
			if (this.searchBar.textboxKeyTyped(p_73869_1_, p_73869_2_)) {
				PacketHandler.INSTANCE.sendToServer(new FRequestMessage(0, master.getX(), master.getY(), master.getZ(), null));
			} else {
				super.keyTyped(p_73869_1_, p_73869_2_);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (i > (guiLeft + 7) && i < (guiLeft + xSize - 7) && j > (guiTop + 7) && j < (guiTop + 90)) {
			int mouse = Mouse.getEventDWheel();
			if (mouse == 0)
				return;
			if (mouse > 0 && page > 1)
				page--;
			if (mouse < 0 && page < maxPage)
				page++;
		}

	}

	class Slot {
		Fluid fluid;
		int x, y, size, guiLeft, guiTop;

		public Slot(Fluid fluid, int x, int y, int size, int guiLeft, int guiTop) {
			this.fluid = fluid;
			this.x = x;
			this.y = y;
			this.size = size;
			this.guiLeft = guiLeft;
			this.guiTop = guiTop;
		}

		Minecraft mc = Minecraft.getMinecraft();

		void drawSlot(int mx, int my) {
			GlStateManager.pushMatrix();
			TextureAtlasSprite fluidIcon = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
			if (fluidIcon == null)
				return;

			this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			drawTexturedModalRect(x, y, fluidIcon, 16, 16);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
			String amount = "" + (size < 1000 ? size : size < 1000000 ? size / 1000 : size < 1000000000 ? size / 1000000 : size / 1000000000);
			amount += size < 1000 ? "mB" : size < 1000000 ? "B" : size < 1000000000 ? "KB" : "MB";
			if (ConfigHandler.smallFont) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(.5f, .5f, .5f);
				mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, new ItemStack(Items.command_block_minecart), x * 2 + 16, y * 2 + 16, amount);
				GlStateManager.popMatrix();
			} else
				mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, new ItemStack(Blocks.fire), x, y, amount);
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
				over = fluid;
			}
		}

		void drawTooltip(int mx, int my) {
			if (this.isMouseOverSlot(mx, my)) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					drawHoveringText(Arrays.asList(fluid.getLocalizedName(FluidRegistry.getFluidStack(fluid.getName(), 1))), mx, my, fontRendererObj);
				} else
					drawHoveringText(Arrays.asList(new String[] { "Amount: " + String.valueOf(size) + " mB" }), mx, my);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		private boolean isMouseOverSlot(int mouseX, int mouseY) {
			return isPointInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
		}
	}

	class Button extends GuiButton {

		public Button(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, String p_i1021_6_) {
			super(p_i1021_1_, p_i1021_2_, p_i1021_3_, 14, 14, p_i1021_6_);
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
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 162 + 14 * k, 0, 14, 14);
				if (id == 0) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 176 + (tile.downwards ? 6 : 0), 14, 6, 8);
				}
				if (id == 1) {
					this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 3, 188 + (tile.sort == Sort.AMOUNT ? 6 : tile.sort == Sort.MOD ? 12 : 0), 14, 6, 8);

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

				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}
	}
}
