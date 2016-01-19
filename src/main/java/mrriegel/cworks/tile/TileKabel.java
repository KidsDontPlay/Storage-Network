package mrriegel.cworks.tile;

import java.util.HashSet;
import java.util.Set;

import scala.collection.parallel.BucketCombiner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import mrriegel.cworks.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileKabel extends TileEntity {
	private Set<EnumFacing> connections;
	private Kind kind;
	private BlockPos master;
	private int priority;

	public enum Kind {
		kabel, exKabel, imKabel, storageKabel, vacuumKabel;
	}

	public TileKabel() {
	}

	public TileKabel(World w, Block b) {
		connections = new HashSet<EnumFacing>();
		kind = getKind(w, b);
	}

	public static Kind getKind(World world, Block b) {
		if (b == ModBlocks.kabel)
			return Kind.kabel;
		if (b == ModBlocks.exKabel)
			return Kind.exKabel;
		if (b == ModBlocks.imKabel)
			return Kind.imKabel;
		if (b == ModBlocks.storageKabel)
			return Kind.storageKabel;
		if (b == ModBlocks.vacuumKabel)
			return Kind.vacuumKabel;
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		connections = new Gson().fromJson(compound.getString("connections"),
				new TypeToken<Set<EnumFacing>>() {
				}.getType());
		kind = Kind.valueOf(compound.getString("kind"));
		master = new Gson().fromJson(compound.getString("master"),
				new TypeToken<BlockPos>() {
				}.getType());

	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("connections", new Gson().toJson(connections));
		if (kind == null)
			kind = getKind(worldObj, worldObj.getBlockState(pos).getBlock());
		compound.setString("kind", kind.toString());
		compound.setString("master", new Gson().toJson(master));
	}

	@Override
	public void onLoad() {
		// if (master != null)
		// ((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

	public Set<EnumFacing> getConnections() {
		return connections;
	}

	public void setConnections(Set<EnumFacing> connections) {
		this.connections = connections;
	}

	public BlockPos getMaster() {
		return master;
	}

	public void setMaster(BlockPos master) {
		this.master = master;
	}

	public Kind getKind() {
		if (kind == null)
			kind = getKind(worldObj, worldObj.getBlockState(pos).getBlock());
		return kind;
	}

}
