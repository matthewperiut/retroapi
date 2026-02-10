package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.modificationstation.stationapi.api.nbt.StationNbtCompound;
import net.ornithemc.feather.constants.NbtTypes;

public class NbtCompound extends NbtElement implements StationNbtCompound {
	private Map elements = new HashMap();

	@Override
	void write(DataOutput output) {
		for (NbtElement nbtElement : this.elements.values()) {
			NbtElement.serialize(nbtElement, output);
		}

		output.writeByte(0);
	}

	@Override
	void read(DataInput input) {
		this.elements.clear();

		NbtElement nbtElement;
		while ((nbtElement = NbtElement.deserialize(input)).getType() != 0) {
			this.elements.put(nbtElement.getName(), nbtElement);
		}
	}

	public Collection getValues() {
		return this.elements.values();
	}

	@Override
	public byte getType() {
		return NbtTypes.COMPOUND;
	}

	public void put(String key, NbtElement element) {
		this.elements.put(key, element.setName(key));
	}

	public void putByte(String key, byte value) {
		this.elements.put(key, new NbtByte((byte)value).setName(key));
	}

	public void putShort(String key, short value) {
		this.elements.put(key, new NbtShort((short)value).setName(key));
	}

	public void putInt(String key, int value) {
		this.elements.put(key, new NbtInt(value).setName(key));
	}

	public void putLong(String key, long value) {
		this.elements.put(key, new NbtLong(value).setName(key));
	}

	public void putFloat(String key, float value) {
		this.elements.put(key, new NbtFloat(value).setName(key));
	}

	public void putDouble(String key, double value) {
		this.elements.put(key, new NbtDouble(value).setName(key));
	}

	public void putString(String key, String value) {
		this.elements.put(key, new NbtString(value).setName(key));
	}

	public void putByteArray(String key, byte[] value) {
		this.elements.put(key, new NbtByteArray(value).setName(key));
	}

	public void putCompound(String key, NbtCompound nbt) {
		this.elements.put(key, nbt.setName(key));
	}

	public void putBoolean(String key, boolean value) {
		this.putByte(key, (byte)(value ? 1 : 0));
	}

	public boolean contains(String key) {
		return this.elements.containsKey(key);
	}

	public byte getByte(String key) {
		return !this.elements.containsKey(key) ? 0 : ((NbtByte)this.elements.get(key)).value;
	}

	public short getShort(String key) {
		return !this.elements.containsKey(key) ? 0 : ((NbtShort)this.elements.get(key)).value;
	}

	public int getInt(String key) {
		return !this.elements.containsKey(key) ? 0 : ((NbtInt)this.elements.get(key)).value;
	}

	public long getLong(String key) {
		return !this.elements.containsKey(key) ? 0L : ((NbtLong)this.elements.get(key)).value;
	}

	public float getFloat(String key) {
		return !this.elements.containsKey(key) ? 0.0F : ((NbtFloat)this.elements.get(key)).value;
	}

	public double getDouble(String key) {
		return !this.elements.containsKey(key) ? 0.0 : ((NbtDouble)this.elements.get(key)).value;
	}

	public String getString(String key) {
		return !this.elements.containsKey(key) ? "" : ((NbtString)this.elements.get(key)).value;
	}

	public byte[] getByteArray(String key) {
		return !this.elements.containsKey(key) ? new byte[0] : ((NbtByteArray)this.elements.get(key)).value;
	}

	public NbtCompound getCompound(String key) {
		return !this.elements.containsKey(key) ? new NbtCompound() : (NbtCompound)this.elements.get(key);
	}

	public NbtList getList(String key) {
		return !this.elements.containsKey(key) ? new NbtList() : (NbtList)this.elements.get(key);
	}

	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	public String toString() {
		return "" + this.elements.size() + " entries";
	}
}
