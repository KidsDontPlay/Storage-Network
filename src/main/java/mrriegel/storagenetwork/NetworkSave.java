package mrriegel.storagenetwork;

import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
@Mod.EventBusSubscriber
public class NetworkSave extends WorldSavedData{

    public static final String NAME = StorageNetwork.MODID + "data";
    public static List<Network> networks = new ArrayList<>();

    public NetworkSave() {
        super(NAME);
    }

    public static NetworkSave getOrLoad(World world){
        MapStorage storage = world.getMapStorage();
        WorldSavedData data = storage.getOrLoadData(NetworkSave.class, NAME);
        if(data instanceof NetworkSave){
            return (NetworkSave) data;
        } else {
            NetworkSave networkSave = new NetworkSave();
            storage.setData(NAME, networkSave);
            return networkSave;
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event){
        getOrLoad(event.getWorld());
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event){
        getOrLoad(event.getWorld());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        for(String key : nbt.getKeySet()){
            Network network = new Network();
            network.corePosition = BlockPos.fromLong(Long.parseLong(key));
            network.deserializeNBT(nbt.getCompoundTag(key));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        for(Network network : networks){
            nbt.setTag(String.valueOf(network.corePosition.toLong()), network.serializeNBT());
        }
        return nbt;
    }

    public static Network getNetwork(GlobalBlockPos pos){
        for(Network network : networks){
            if(network != null && network.corePosition != null){
                if(network.corePosition.equals(pos)){
                    return network;
                }
            }
        }
        return null;
    }

    public static void removeNetwork(GlobalBlockPos pos){
        Network network = getNetwork(pos);
        if(network != null){
            networks.remove(network);
        }
    }

}
