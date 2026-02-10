package com.periut.retroapi.registry;

import com.periut.retroapi.api.RetroIdentifier;
import net.minecraft.item.Item;

public class ItemRegistration {
	private final RetroIdentifier id;
	private final Item item;

	public ItemRegistration(RetroIdentifier id, Item item) {
		this.id = id;
		this.item = item;
	}

	public RetroIdentifier getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}
}
