package com.periut.retroapi.api;

import com.periut.retroapi.compat.StationAPICompat;
import com.periut.retroapi.registry.ItemRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;

public class RetroItem extends Item {
	private static int nextPlaceholderId = 2000;

	public RetroItem() {
		super(allocatePlaceholderItemId());
	}

	@Override
	public RetroItem setMaxStackSize(int maxStackSize) {
		super.setMaxStackSize(maxStackSize);
		return this;
	}

	@Override
	public RetroItem setMaxDamage(int maxDamage) {
		super.setMaxDamage(maxDamage);
		return this;
	}

	@Override
	public RetroItem setKey(String key) {
		super.setKey(key);
		return this;
	}

	/**
	 * Convenience method to register a texture for this item.
	 * Texture file: assets/{id.namespace()}/retroapi/textures/item/{id.path()}.png
	 */
	public RetroItem texture(RetroIdentifier textureId) {
		RetroTexture tex = RetroTextures.addItemTexture(textureId);
		this.setSprite(tex.id);
		RetroTextures.trackItem(this, tex);
		return this;
	}

	/**
	 * Register this item with RetroAPI.
	 * Handles registry and StationAPI compat.
	 */
	public RetroItem register(RetroIdentifier id) {
		RetroRegistry.registerItem(new ItemRegistration(id, this));

		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			StationAPICompat.registerItem(id.namespace(), id.path(), this);
		}

		return this;
	}

	static int allocatePlaceholderItemId() {
		Item[] byId = Item.BY_ID;
		while (byId[nextPlaceholderId + 256] != null) {
			nextPlaceholderId++;
		}
		return nextPlaceholderId++;
	}
}
