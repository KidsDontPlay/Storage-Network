package mrriegel.storagenetwork.blocks;

import mrriegel.storagenetwork.blocks.PropertyConnection.Connect;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyConnection implements IUnlistedProperty<Connect> {
	public enum Connect {
		CONNECT, STORAGE, NULL
	}

	private final String name;

	public PropertyConnection(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid(Connect value) {
		return true;
	}

	@Override
	public Class<Connect> getType() {
		return Connect.class;
	}

	@Override
	public String valueToString(Connect value) {
		return value.toString();
	}
}
