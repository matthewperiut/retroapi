package com.periut.retroapi.registry;

import net.minecraft.item.Item;

public class RetroItemImpl extends Item {
	public RetroItemImpl(int id) {
		super(id);
	}

	@Override
	public RetroItemImpl setMaxStackSize(int maxStackSize) {
		super.setMaxStackSize(maxStackSize);
		return this;
	}

	@Override
	public RetroItemImpl setMaxDamage(int maxDamage) {
		super.setMaxDamage(maxDamage);
		return this;
	}

	@Override
	public RetroItemImpl setKey(String key) {
		super.setKey(key);
		return this;
	}
}
