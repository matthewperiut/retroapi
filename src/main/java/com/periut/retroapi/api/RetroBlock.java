package com.periut.retroapi.api;

import com.periut.retroapi.compat.StationAPICompat;
import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;

public class RetroBlock extends Block {
	private static int nextPlaceholderId = 200;

	public RetroBlock(Material material) {
		super(allocatePlaceholderBlockId(), material);
	}

	@Override
	public RetroBlock setSounds(Block.Sounds sounds) {
		super.setSounds(sounds);
		return this;
	}

	@Override
	public RetroBlock setStrength(float strength) {
		super.setStrength(strength);
		return this;
	}

	@Override
	public RetroBlock setBlastResistance(float resistance) {
		super.setBlastResistance(resistance);
		return this;
	}

	@Override
	public RetroBlock setLight(float light) {
		super.setLight(light);
		return this;
	}

	@Override
	public RetroBlock setOpacity(int opacity) {
		super.setOpacity(opacity);
		return this;
	}

	@Override
	public RetroBlock setKey(String key) {
		super.setKey(key);
		return this;
	}

	/**
	 * Set the sprite index for this block (used for particles and fallback rendering).
	 */
	public void setSprite(int spriteId) {
		this.sprite = spriteId;
	}

	/**
	 * Convenience method to register a single texture for all faces.
	 * Texture file: assets/{id.namespace()}/retroapi/textures/block/{id.path()}.png
	 */
	public RetroBlock texture(RetroIdentifier textureId) {
		RetroTexture tex = RetroTextures.addBlockTexture(textureId);
		this.sprite = tex.id;
		RetroTextures.trackBlock(this, tex);
		return this;
	}

	/**
	 * Register this block with RetroAPI.
	 * Handles BlockItem creation, registry, and StationAPI compat.
	 */
	public RetroBlock register(RetroIdentifier id) {
		boolean hasStationAPI = FabricLoader.getInstance().isModLoaded("stationapi");

		BlockItem blockItem = null;
		if (!hasStationAPI) {
			blockItem = new BlockItem(this.id - 256);
		}

		RetroRegistry.registerBlock(new BlockRegistration(id, this, blockItem));

		if (hasStationAPI) {
			StationAPICompat.registerBlock(id.namespace(), id.path(), this);
		}

		return this;
	}

	static int allocatePlaceholderBlockId() {
		Block[] byId = Block.BY_ID;
		while (nextPlaceholderId < 256 && byId[nextPlaceholderId] != null) {
			nextPlaceholderId++;
		}
		if (nextPlaceholderId >= 256) {
			throw new RuntimeException("No more placeholder block IDs available (0-255 exhausted)");
		}
		return nextPlaceholderId++;
	}
}
