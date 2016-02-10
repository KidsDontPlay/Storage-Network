package mrriegel.storagenetwork.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.blocks.BlockKabel;
import mrriegel.storagenetwork.blocks.PropertyConnection.Connect;
import mrriegel.storagenetwork.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.google.common.primitives.Ints;

public class CableModel implements ISmartBlockModel {

	public static final ModelResourceLocation kabel = new ModelResourceLocation(StorageNetwork.MODID + ":kabel");
	public static final ModelResourceLocation ex = new ModelResourceLocation(StorageNetwork.MODID + ":exKabel");
	public static final ModelResourceLocation im = new ModelResourceLocation(StorageNetwork.MODID + ":imKabel");
	public static final ModelResourceLocation storage = new ModelResourceLocation(StorageNetwork.MODID + ":storageKabel");
	public static final ModelResourceLocation vacuum = new ModelResourceLocation(StorageNetwork.MODID + ":vacuumKabel");
	IBakedModel model = null;

	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Connect north = extendedBlockState.getValue(BlockKabel.NORTH);
		Connect south = extendedBlockState.getValue(BlockKabel.SOUTH);
		Connect west = extendedBlockState.getValue(BlockKabel.WEST);
		Connect east = extendedBlockState.getValue(BlockKabel.EAST);
		Connect up = extendedBlockState.getValue(BlockKabel.UP);
		Connect down = extendedBlockState.getValue(BlockKabel.DOWN);
		TextureAtlasSprite node = null;
		TextureAtlasSprite line = null;
		if (state.getBlock() == ModBlocks.kabel) {
			node = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/kabelN");
			line = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/kabel");
		} else if (state.getBlock() == ModBlocks.exKabel) {
			node = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/exKabelN");
			line = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/exKabel");
		} else if (state.getBlock() == ModBlocks.imKabel) {
			node = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/imKabelN");
			line = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/imKabel");
		} else if (state.getBlock() == ModBlocks.storageKabel) {
			node = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/storageKabelN");
			line = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/storageKabel");
		} else if (state.getBlock() == ModBlocks.vacuumKabel) {
			node = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/vacuumKabelN");
			line = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(StorageNetwork.MODID + ":blocks/vacuumKabel");
		}
		model = new BakedModel(node, line, north, south, west, east, up, down);
		return model;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return model == null ? null : model.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return null;
	}

	public class BakedModel implements IBakedModel {
		private TextureAtlasSprite node;
		private TextureAtlasSprite line;
		private TextureAtlasSprite sprite;

		private final Connect north;
		private final Connect south;
		private final Connect west;
		private final Connect east;
		private final Connect up;
		private final Connect down;

		public BakedModel(TextureAtlasSprite node, TextureAtlasSprite line, Connect north, Connect south, Connect west, Connect east, Connect up, Connect down) {
			this.node = node;
			this.line = line;
			this.north = north;
			this.south = south;
			this.west = west;
			this.east = east;
			this.up = up;
			this.down = down;
		}

		private int[] vertexToInts(double x, double y, double z, float u, float v) {
			return new int[] { Float.floatToRawIntBits((float) x), Float.floatToRawIntBits((float) y), Float.floatToRawIntBits((float) z), -1, Float.floatToRawIntBits(sprite.getInterpolatedU(u)), Float.floatToRawIntBits(sprite.getInterpolatedV(v)), 0 };
		}

		private BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
			Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
			EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);

			return new BakedQuad(Ints.concat(vertexToInts(v1.xCoord, v1.yCoord, v1.zCoord, 0, 0), vertexToInts(v2.xCoord, v2.yCoord, v2.zCoord, 0, 16), vertexToInts(v3.xCoord, v3.yCoord, v3.zCoord, 16, 16), vertexToInts(v4.xCoord, v4.yCoord, v4.zCoord, 16, 0)), -1, side);
		}

		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
			return Collections.EMPTY_LIST;
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			List<BakedQuad> quads = new ArrayList<BakedQuad>();
			double o = .3125;
			sprite = node;
			BakedQuad north = createQuad(new Vec3(o, 1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o, o), new Vec3(o, o, o));
			BakedQuad south = createQuad(new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, o, 1 - o), new Vec3(1 - o, o, 1 - o));
			BakedQuad west = createQuad(new Vec3(o, o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o), new Vec3(o, o, o));
			BakedQuad east = createQuad(new Vec3(1 - o, o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, o, 1 - o));
			BakedQuad up = createQuad(new Vec3(1 - o, 1 - o, o), new Vec3(o, 1 - o, o), new Vec3(o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o));
			BakedQuad down = createQuad(new Vec3(1 - o, o, 1 - o), new Vec3(o, o, 1 - o), new Vec3(o, o, o), new Vec3(1 - o, o, o));
			if (!oo() || !node.toString().contains("kabel")) {
				quads.add(north);
				quads.add(south);
				quads.add(west);
				quads.add(east);
				quads.add(up);
				quads.add(down);
			}
			sprite = line;
			o = .375;
			if (connected(this.up)) {
				quads.add(createQuad(new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1, o), new Vec3(1 - o, 1, 1 - o), new Vec3(1 - o, 1 - o, 1 - o)));
				quads.add(createQuad(new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1, 1 - o), new Vec3(o, 1, o), new Vec3(o, 1 - o, o)));
				quads.add(createQuad(new Vec3(o, 1, o), new Vec3(1 - o, 1, o), new Vec3(1 - o, 1 - o, o), new Vec3(o, 1 - o, o)));
				quads.add(createQuad(new Vec3(o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1, 1 - o), new Vec3(o, 1, 1 - o)));
			} else {
				quads.add(createQuad(new Vec3(o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, o), new Vec3(o, 1 - o, o)));
			}
			if (connected(this.down)) {
				quads.add(createQuad(new Vec3(1 - o, 0, o), new Vec3(1 - o, o, o), new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 0, 1 - o)));
				quads.add(createQuad(new Vec3(o, 0, 1 - o), new Vec3(o, o, 1 - o), new Vec3(o, o, o), new Vec3(o, 0, o)));
				quads.add(createQuad(new Vec3(o, o, o), new Vec3(1 - o, o, o), new Vec3(1 - o, 0, o), new Vec3(o, 0, o)));
				quads.add(createQuad(new Vec3(o, 0, 1 - o), new Vec3(1 - o, 0, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(o, o, 1 - o)));
			} else {
				quads.add(createQuad(new Vec3(o, o, o), new Vec3(1 - o, o, o), new Vec3(1 - o, o, 1 - o), new Vec3(o, o, 1 - o)));
			}

			if (connected(this.east)) {
				quads.add(createQuad(new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1, 1 - o, 1 - o), new Vec3(1, 1 - o, o), new Vec3(1 - o, 1 - o, o)));
				quads.add(createQuad(new Vec3(1 - o, o, o), new Vec3(1, o, o), new Vec3(1, o, 1 - o), new Vec3(1 - o, o, 1 - o)));
				quads.add(createQuad(new Vec3(1 - o, 1 - o, o), new Vec3(1, 1 - o, o), new Vec3(1, o, o), new Vec3(1 - o, o, o)));
				quads.add(createQuad(new Vec3(1 - o, o, 1 - o), new Vec3(1, o, 1 - o), new Vec3(1, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o)));
			} else {
				quads.add(createQuad(new Vec3(1 - o, o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, o, 1 - o)));
			}

			if (connected(this.west)) {
				quads.add(createQuad(new Vec3(0, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o), new Vec3(0, 1 - o, o)));
				quads.add(createQuad(new Vec3(0, o, o), new Vec3(o, o, o), new Vec3(o, o, 1 - o), new Vec3(0, o, 1 - o)));
				quads.add(createQuad(new Vec3(0, 1 - o, o), new Vec3(o, 1 - o, o), new Vec3(o, o, o), new Vec3(0, o, o)));
				quads.add(createQuad(new Vec3(0, o, 1 - o), new Vec3(o, o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(0, 1 - o, 1 - o)));
			} else {
				quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o), new Vec3(o, o, o)));
			}

			if (connected(this.north)) {
				quads.add(createQuad(new Vec3(o, 1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 0), new Vec3(o, 1 - o, 0)));
				quads.add(createQuad(new Vec3(o, o, 0), new Vec3(1 - o, o, 0), new Vec3(1 - o, o, o), new Vec3(o, o, o)));
				quads.add(createQuad(new Vec3(1 - o, o, 0), new Vec3(1 - o, 1 - o, 0), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o, o)));
				quads.add(createQuad(new Vec3(o, o, o), new Vec3(o, 1 - o, o), new Vec3(o, 1 - o, 0), new Vec3(o, o, 0)));
			} else {
				quads.add(createQuad(new Vec3(o, 1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o, o), new Vec3(o, o, o)));
			}
			if (connected(this.south)) {
				quads.add(createQuad(new Vec3(o, 1 - o, 1), new Vec3(1 - o, 1 - o, 1), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o)));
				quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, o, 1), new Vec3(o, o, 1)));
				quads.add(createQuad(new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1), new Vec3(1 - o, o, 1)));
				quads.add(createQuad(new Vec3(o, o, 1), new Vec3(o, 1 - o, 1), new Vec3(o, 1 - o, 1 - o), new Vec3(o, o, 1 - o)));
			} else {
				quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o)));
			}
			return quads;
		}

		private boolean oo() {
			boolean a = connected(north) && connected(south) && !connected(west) && !connected(east) && !connected(up) && !connected(down);
			boolean b = !connected(north) && !connected(south) && connected(west) && connected(east) && !connected(up) && !connected(down);
			boolean c = !connected(north) && !connected(south) && !connected(west) && !connected(east) && connected(up) && connected(down);
			return a ^ b ^ c;
		}

		private boolean connected(Connect c) {
			return c == Connect.STORAGE || c == Connect.CONNECT;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return node;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

	}

}
