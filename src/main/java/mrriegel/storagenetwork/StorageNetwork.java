package mrriegel.storagenetwork;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mrriegel.storagenetwork.gui.request.ContainerRequest;
import mrriegel.storagenetwork.helper.NBTHelper;
import mrriegel.storagenetwork.proxy.CommonProxy;
import mrriegel.storagenetwork.tile.TileKabel.Kind;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = StorageNetwork.MODID, name = StorageNetwork.MODNAME, version = StorageNetwork.VERSION, guiFactory = "mrriegel.storagenetwork.config.GuiFactory")
public class StorageNetwork {
	public static final String MODID = "storagenetwork";
	public static final String VERSION = "1.7.1";
	public static final String MODNAME = "Storage Network";

	@Instance(StorageNetwork.MODID)
	public static StorageNetwork instance;

	@SidedProxy(clientSide = "mrriegel.storagenetwork.proxy.ClientProxy", serverSide = "mrriegel.storagenetwork.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setString("ContainerClass", ContainerRequest.class.getName());
		tagCompound.setBoolean("PhantomItems", false);
		tagCompound.setString("AlignToGrid", "left");
		FMLInterModComms.sendMessage("craftingtweaks", "RegisterProvider", tagCompound);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
