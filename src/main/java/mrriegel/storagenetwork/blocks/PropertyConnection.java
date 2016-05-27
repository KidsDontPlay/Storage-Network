package mrriegel.storagenetwork.blocks;

import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.sun.jndi.cosnaming.CNNameParser;

import mrriegel.storagenetwork.blocks.PropertyConnection.Connect;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyHelper;

public class PropertyConnection implements IProperty<Connect> {

	private final ImmutableSet<Connect> allowedValues = ImmutableSet.<Connect> of(Connect.CONNECT, Connect.STORAGE, Connect.NULL);

	public static enum Connect {
		CONNECT, STORAGE, NULL
	}

	String name;

	public PropertyConnection(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<Connect> getAllowedValues() {
		return allowedValues;
	}

	@Override
	public Class<Connect> getValueClass() {
		return Connect.class;
	}

	@Override
	public Optional<Connect> parseValue(String value) {
		try {
			return Optional.of(Connect.valueOf(value.toUpperCase()));
		} catch (Exception e) {
			return Optional.<Connect> absent();
		}
	}

	@Override
	public String getName(Connect value) {
		return value.toString().toLowerCase();
	}

}
