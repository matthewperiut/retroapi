package com.periut.retroapi.registry;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Sounds;
import net.minecraft.block.material.Material;

public class RetroBlockImpl extends Block {
	public RetroBlockImpl(int id, Material material) {
		super(id, material);
	}

	@Override
	public RetroBlockImpl setSounds(Block.Sounds sounds) {
		super.setSounds(sounds);
		return this;
	}

	@Override
	public RetroBlockImpl setStrength(float strength) {
		super.setStrength(strength);
		return this;
	}

	@Override
	public RetroBlockImpl setBlastResistance(float resistance) {
		super.setBlastResistance(resistance);
		return this;
	}

	@Override
	public RetroBlockImpl setLight(float light) {
		super.setLight(light);
		return this;
	}

	@Override
	public RetroBlockImpl setOpacity(int opacity) {
		super.setOpacity(opacity);
		return this;
	}

	@Override
	public RetroBlockImpl setKey(String key) {
		super.setKey(key);
		return this;
	}
}
