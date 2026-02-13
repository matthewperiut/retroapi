package com.periut.retroapi.api;

import com.periut.retroapi.mixin.BlockAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Duck interface injected onto all Blocks via mixin.
 * Provides RetroAPI functionality without requiring subclassing.
 * All methods are no-ops by default until explicitly called.
 */
public interface RetroBlockAccess {
	/** Access-widening wrapper for {@link Block#setSounds}. */
	RetroBlockAccess retroapi$setSounds(Block.Sounds sounds);

	/** Access-widening wrapper for {@link Block#setStrength}. */
	RetroBlockAccess retroapi$setStrength(float strength);

	/** Access-widening wrapper for {@link Block#setBlastResistance}. */
	RetroBlockAccess retroapi$setBlastResistance(float resistance);

	/** Access-widening wrapper for {@link Block#setLight}. */
	RetroBlockAccess retroapi$setLight(float light);

	/** Access-widening wrapper for {@link Block#setOpacity}. */
	RetroBlockAccess retroapi$setOpacity(int opacity);

	/**
	 * Mark this block as non-solid (not a full opaque cube).
	 * This allows neighboring blocks to render their adjacent faces
	 * and lets light pass through.
	 */
	RetroBlockAccess retroapi$setSolidRender(boolean solid);

	/**
	 * Set persistent block bounds for collision, selection, and rendering.
	 * Unlike {@link Block#setShape}, these bounds survive inventory rendering resets.
	 */
	RetroBlockAccess retroapi$setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	/**
	 * Set the render type for this block using a flattened identifier.
	 * The identifier is resolved to a numeric ID via {@link RenderType#resolve}.
	 *
	 * @see RenderType for vanilla and custom render type identifiers
	 */
	RetroBlockAccess retroapi$setRenderType(RetroIdentifier renderTypeId);

	/**
	 * Set the sprite index for this block (used for particles and fallback rendering).
	 */
	void retroapi$setSprite(int spriteId);

	/**
	 * Convenience method to register a single texture for all faces.
	 * Texture file: assets/{id.namespace()}/retroapi/textures/block/{id.path()}.png
	 */
	RetroBlockAccess retroapi$texture(RetroIdentifier textureId);

	/**
	 * Register this block with RetroAPI.
	 * Handles BlockItem creation, registry, and StationAPI compat.
	 */
	Block retroapi$register(RetroIdentifier id);

	/**
	 * Create a new Block with an automatically allocated placeholder ID.
	 *
	 * @param material the block material
	 * @return a new Block instance with RetroAPI functionality available via cast
	 */
	static Block create(Material material) {
		return BlockAccessor.retroapi$create(allocatePlaceholderBlockId(), material);
	}

	/**
	 * Allocate a placeholder block ID (200-255 range).
	 * Useful for subclasses that need to pass an ID to {@code super()}.
	 */
	static int allocatePlaceholderBlockId() {
		Block[] byId = Block.BY_ID;
		for (int i = 200; i < 256; i++) {
			if (byId[i] == null) {
				return i;
			}
		}
		throw new RuntimeException("No more placeholder block IDs available (0-255 exhausted)");
	}
}
