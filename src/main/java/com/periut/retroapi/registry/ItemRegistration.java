package com.periut.retroapi.registry;

import com.periut.retroapi.api.RetroIdentifier;
import net.minecraft.item.Item;

public class ItemRegistration {
	private final RetroIdentifier id;
	private final Item item;
	private final String textureNamespace;
	private final String texturePath;

	public ItemRegistration(RetroIdentifier id, Item item, String textureNamespace, String texturePath) {
		this.id = id;
		this.item = item;
		this.textureNamespace = textureNamespace;
		this.texturePath = texturePath;
	}

	public RetroIdentifier getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public String getTextureNamespace() {
		return textureNamespace;
	}

	public String getTexturePath() {
		return texturePath;
	}
}
