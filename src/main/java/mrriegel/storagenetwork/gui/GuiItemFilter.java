package mrriegel.storagenetwork.gui;

import java.io.IOException;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.limelib.gui.button.GuiButtonTooltip;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.storagenetwork.container.ContainerItemFilter;
import mrriegel.storagenetwork.message.MessageItemFilter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class GuiItemFilter extends CommonGuiContainer {

	ItemStack stack;

	GuiButtonTooltip meta, nbt, ore, mod, white;

	public GuiItemFilter(ContainerItemFilter container) {
		super(container);
		stack = container.stack;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(meta = new GuiButtonSimple(0, guiLeft + 120, guiTop + 7, 45, 12, "", null));
		buttonList.add(nbt = new GuiButtonSimple(1, guiLeft + 120, guiTop + 21, 45, 12, "", null));
		buttonList.add(ore = new GuiButtonSimple(2, guiLeft + 120, guiTop + 35, 45, 12, "", null));
		buttonList.add(mod = new GuiButtonSimple(3, guiLeft + 120, guiTop + 49, 45, 12, "", null));
		buttonList.add(white = new GuiButtonSimple(4, guiLeft + 120, guiTop + 63, 45, 12, "", null));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		meta.displayString = (NBTStackHelper.getBoolean(stack, "meta") ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString()) + "META";
		nbt.displayString = (NBTStackHelper.getBoolean(stack, "nbt") ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString()) + "NBT";
		ore.displayString = (NBTStackHelper.getBoolean(stack, "ore") ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString()) + "ORE";
		mod.displayString = (NBTStackHelper.getBoolean(stack, "mod") ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString()) + "MOD";
		white.displayString = (NBTStackHelper.getBoolean(stack, "white") ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString()) + "WHITE";
		meta.setTooltip((NBTStackHelper.getBoolean(stack, "meta") ? "Meta data will be considered." : "Meta data won't be considered."));
		nbt.setTooltip((NBTStackHelper.getBoolean(stack, "nbt") ? "NBT data will be considered." : "NBT data won't be considered."));
		ore.setTooltip((NBTStackHelper.getBoolean(stack, "ore") ? "Ore dictionary will be considered." : "Ore dictionary won't be considered."));
		mod.setTooltip((NBTStackHelper.getBoolean(stack, "mod") ? "Mod will be considered." : "Mod won't be considered."));
		white.setTooltip((NBTStackHelper.getBoolean(stack, "white") ? "Whitelist" : "Blacklist"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(7, 83);
		drawer.drawSlots(7, 7, 6, 4);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("buttonID", button.id);
		PacketHandler.sendToServer(new MessageItemFilter(nbt));
		switch (nbt.getInteger("buttonID")) {
		case 0:
			NBTStackHelper.setBoolean(stack, "meta", !NBTStackHelper.getBoolean(stack, "meta"));
			break;
		case 1:
			NBTStackHelper.setBoolean(stack, "nbt", !NBTStackHelper.getBoolean(stack, "nbt"));
			break;
		case 2:
			NBTStackHelper.setBoolean(stack, "ore", !NBTStackHelper.getBoolean(stack, "ore"));
			break;
		case 3:
			NBTStackHelper.setBoolean(stack, "mod", !NBTStackHelper.getBoolean(stack, "mod"));
			break;
		case 4:
			NBTStackHelper.setBoolean(stack, "white", !NBTStackHelper.getBoolean(stack, "white"));
			break;
		default:
			break;
		}
	}

}
