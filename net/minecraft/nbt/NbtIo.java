package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NbtIo {
	public static NbtCompound readCompressed(InputStream is) {
		DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(is));

		NbtCompound nbtCompound;
		try {
			nbtCompound = read(dataInputStream);
		} finally {
			dataInputStream.close();
		}

		return nbtCompound;
	}

	public static void writeCompressed(NbtCompound nbt, OutputStream os) {
		DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(os));

		try {
			write(nbt, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	public static NbtCompound read(DataInput input) {
		NbtElement nbtElement = NbtElement.deserialize(input);
		if (nbtElement instanceof NbtCompound) {
			return (NbtCompound)nbtElement;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void write(NbtCompound nbt, DataOutput output) {
		NbtElement.serialize(nbt, output);
	}
}
