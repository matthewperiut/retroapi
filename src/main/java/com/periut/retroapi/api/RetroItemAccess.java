package com.periut.retroapi.api;

import net.minecraft.item.Item;

/**
 * Duck interface injected onto all Items via mixin.
 * Provides RetroAPI functionality without requiring subclassing.
 */
public interface RetroItemAccess {
	/**
	 * Convenience method to register a texture for this item.
	 * Texture file: assets/{id.namespace()}/retroapi/textures/item/{id.path()}.png
	 */
	RetroItemAccess retroapi$texture(RetroIdentifier textureId);

	/**
	 * Register this item with RetroAPI.
	 * Handles registry and StationAPI compat.
	 */
	Item retroapi$register(RetroIdentifier id);

	/**
	 * Create a new Item with an automatically allocated placeholder ID.
	 *
	 * @return a new Item instance with RetroAPI functionality available via cast
	 */
	static Item create() {
		return new Item(allocatePlaceholderItemId());
	}

	/**
	 * Allocate a placeholder item ID (2000+ range).
	 * Useful for subclasses that need to pass an ID to {@code super()}.
	 */
	static int allocatePlaceholderItemId() {
		Item[] byId = Item.BY_ID;
		for (int i = 2000 + 256; i < byId.length; i++) {
			if (byId[i] == null) {
				return i - 256;
			}
		}
		throw new RuntimeException("No more placeholder item IDs available");
	}
}
