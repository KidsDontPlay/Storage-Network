package mrriegel.storagenetwork.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class SNPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RequestRecipeTransferHandler());
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(new TemplateRecipeTransferHandler());
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}
}
