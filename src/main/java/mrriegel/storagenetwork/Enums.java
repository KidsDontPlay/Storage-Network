package mrriegel.storagenetwork;

import net.minecraft.util.IStringSerializable;

public class Enums {

	public enum IOMODE {
		IN, OUT, INOUT;

		public boolean canInsert() {
			return this == IN || this == INOUT;
		}

		public boolean canExtract() {
			return this == OUT || this == INOUT;
		}

		public IOMODE next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
	}

	public enum Connect implements IStringSerializable {
		NULL("null"), CABLE("cable"), TILE("tile");

		String name;

		Connect(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

}
