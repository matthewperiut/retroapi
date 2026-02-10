package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Block.Sounds {
	public final String key;
	public final float volume;
	public final float pitch;

	public Block.Sounds(String key, float volume, float pitch) {
		this.key = key;
		this.volume = volume;
		this.pitch = pitch;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public String getBreaking() {
		return "step." + this.key;
	}

	public String getStepping() {
		return "step." + this.key;
	}
}
