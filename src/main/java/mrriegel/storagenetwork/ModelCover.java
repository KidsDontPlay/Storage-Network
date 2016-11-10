package mrriegel.storagenetwork;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModelCover implements IBakedModel {

	//	IBakedModel naked;

	public static final ModelResourceLocation blockStatesFileName = new ModelResourceLocation(Registry.networkCable.getRegistryName().toString());

	public static final ModelResourceLocation variantTag = new ModelResourceLocation(Registry.networkCable.getRegistryName().toString(), "normal");

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		//		if (renderNakedModel(state))
		//			return naked.getQuads(state, side, rand);
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.GOLD_BLOCK.getDefaultState()).getQuads(state, side, rand);
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
		return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(Blocks.GOLD_BLOCK.getRegistryName().toString());
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		event.getModelRegistry().putObject(variantTag, new ModelCover());
	}

}
