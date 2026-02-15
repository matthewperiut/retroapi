package com.periut.retroapi.register.block;

import com.periut.retroapi.register.RetroIdentifier;

/**
 * Represents a registered texture with a mutable sprite index.
 * The {@link #id} field is updated when the atlas is resolved
 * (during StationAPI's TextureRegisterEvent or our own atlas compositing).
 * <p>
 * Pass this object around and read .id at render time to get the correct atlas index.
 */
public class RetroTexture {
	/** The sprite index in the atlas. Updated during texture resolution. */
	public int id;
	private final RetroIdentifier identifier;

	RetroTexture(RetroIdentifier identifier, int initialId) {
		this.identifier = identifier;
		this.id = initialId;
	}

	public RetroIdentifier getIdentifier() {
		return identifier;
	}
}
