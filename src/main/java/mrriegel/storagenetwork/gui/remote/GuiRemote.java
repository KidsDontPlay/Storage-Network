package mrriegel.storagenetwork.gui.remote;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.config.ConfigHandler;
import mrriegel.storagenetwork.gui.AbstractGuiRequest;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.items.ItemRemote;
import mrriegel.storagenetwork.tile.TileRequest.Sort;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.input.Keyboard;

public class GuiRemote extends AbstractGuiRequest {

	public GuiRemote(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		texture = new ResourceLocation(StorageNetwork.MODID + ":textures/gui/remote.png");
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
		direction = new Button(0, guiLeft + 7, guiTop + 93 + 64, "");
		buttonList.add(direction);
		sort = new Button(1, guiLeft + 21, guiTop + 93 + 64, "");
		buttonList.add(sort);
		left = new Button(2, guiLeft + 44, guiTop + 93 + 64, "<");
		buttonList.add(left);
		right = new Button(3, guiLeft + 58, guiTop + 93 + 64, ">");
		buttonList.add(right);
		jei = new Button(4, guiLeft + 169, guiTop + 93 + 64, "");
		if (ConfigHandler.jeiLoaded)
			buttonList.add(jei);

	}

	@Override
	protected int getLines() {
		return 7;
	}

	@Override
	protected int getColumns() {
		return 8;
	}

	@Override
	protected boolean getDownwards() {
		return NBTHelper.getBoolean(mc.thePlayer.inventory.getCurrentItem(), "down");
	}

	@Override
	protected void setDownwards(boolean d) {
		NBTHelper.setBoolean(mc.thePlayer.inventory.getCurrentItem(), "down", d);

	}

	@Override
	protected Sort getSort() {
		return Sort.valueOf(NBTHelper.getString(mc.thePlayer.inventory.getCurrentItem(), "sort"));
	}

	@Override
	protected void setSort(Sort s) {
		NBTHelper.setString(mc.thePlayer.inventory.getCurrentItem(), "sort", s.toString());
	}

	@Override
	protected BlockPos getPos() {
		return BlockPos.ORIGIN;
	}

	@Override
	protected BlockPos getMaster() {
		return ItemRemote.getTile(mc.thePlayer.inventory.getCurrentItem()).getPos();
	}

	@Override
	protected int getDim() {
		return NBTHelper.getInteger(mc.thePlayer.inventory.getCurrentItem(), "dim");
	}

	@Override
	protected boolean inField(int mouseX, int mouseY) {
		return mouseX > (guiLeft + 7) && mouseX < (guiLeft + xSize - 7) && mouseY > (guiTop + 7) && mouseY < (guiTop + 90 + 64);
	}

	@Override
	protected boolean inSearchbar(int mouseX, int mouseY) {
		return isPointInRegion(81, 96 + 64, 85, fontRendererObj.FONT_HEIGHT, mouseX, mouseY);
	}

	@Override
	protected boolean inX(int mouseX, int mouseY) {
		return false;
	}

}
