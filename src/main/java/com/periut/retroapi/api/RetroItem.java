package com.periut.retroapi.api;

import com.periut.retroapi.registry.ItemRegistration;
import com.periut.retroapi.registry.RetroItemImpl;
import com.periut.retroapi.registry.RetroRegistry;
import net.minecraft.item.Item;

public class RetroItem {
	private static int nextPlaceholderId = 2000;

	private final RetroIdentifier id;
	private int maxStackSize = 64;
	private int maxDamage = 0;
	private String translationKey;
	private String texturePath;

	private RetroItem(RetroIdentifier id) {
		this.id = id;
		this.translationKey = id.path();
		this.texturePath = id.path();
	}

	public static RetroItem create(RetroIdentifier id) {
		return new RetroItem(id);
	}

	public RetroItem maxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
		return this;
	}

	public RetroItem maxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
		return this;
	}

	public RetroItem translationKey(String translationKey) {
		this.translationKey = translationKey;
		return this;
	}

	public RetroItem texturePath(String texturePath) {
		this.texturePath = texturePath;
		return this;
	}

	static int allocatePlaceholderItemId() {
		// Item constructor takes raw id, internally stores id + 256, BY_ID indexed at id + 256
		Item[] byId = Item.BY_ID;
		while (byId[nextPlaceholderId + 256] != null) {
			nextPlaceholderId++;
		}
		return nextPlaceholderId++;
	}

	public Item build() {
		int placeholderId = allocatePlaceholderItemId();

		RetroItemImpl item = new RetroItemImpl(placeholderId);
		item.setMaxStackSize(maxStackSize);
		if (maxDamage > 0) {
			item.setMaxDamage(maxDamage);
		}
		item.setKey(translationKey);

		RetroRegistry.registerItem(new ItemRegistration(id, item, id.namespace(), texturePath));
		return item;
	}
}
