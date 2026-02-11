package com.periut.retroapi.api;

import com.periut.retroapi.compat.StationAPICompat;
import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.world.WorldView;

public class RetroBlock extends Block {
	private static int nextPlaceholderId = 200;
	private int renderType = 0;
	private boolean solidRender = true;
	private float[] customBounds = null;

	public RetroBlock(Material material) {
		super(allocatePlaceholderBlockId(), material);
		// The super constructor calls this.isSolidRender() before solidRender field is
		// initialized (it's false at that point), setting IS_SOLID_RENDER[id] = false.
		// Repair the array now that solidRender = true from the field initializer.
		Block.IS_SOLID_RENDER[this.id] = true;
		Block.OPACITIES[this.id] = 255;
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
	 * Mark this block as non-solid (not a full opaque cube).
	 * This allows neighboring blocks to render their adjacent faces
	 * and lets light pass through.
	 */
	public RetroBlock setSolidRender(boolean solid) {
		this.solidRender = solid;
		Block.IS_SOLID_RENDER[this.id] = solid;
		Block.OPACITIES[this.id] = solid ? 255 : 0;
		return this;
	}

	@Override
	public boolean isSolidRender() {
		return solidRender;
	}

	@Override
	public boolean isCube() {
		return solidRender;
	}

	/**
	 * Set persistent block bounds for collision, selection, and rendering.
	 * Unlike {@link #setShape}, these bounds survive inventory rendering resets.
	 */
	public RetroBlock setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.customBounds = new float[]{minX, minY, minZ, maxX, maxY, maxZ};
		this.setShape(minX, minY, minZ, maxX, maxY, maxZ);
		return this;
	}

	@Override
	public void updateShape(WorldView world, int x, int y, int z) {
		if (customBounds != null) {
			setShape(customBounds[0], customBounds[1], customBounds[2],
				customBounds[3], customBounds[4], customBounds[5]);
		}
	}

	/**
	 * Set the render type for this block using a flattened identifier.
	 * The identifier is resolved to a numeric ID via {@link RenderType#resolve}.
	 *
	 * @see RenderType for vanilla and custom render type identifiers
	 */
	public RetroBlock setRenderType(RetroIdentifier renderTypeId) {
		this.renderType = RenderType.resolve(renderTypeId);
		return this;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getRenderType() {
		return renderType;
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
