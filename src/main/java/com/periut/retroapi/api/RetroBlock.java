package com.periut.retroapi.api;

import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.RetroBlockImpl;
import com.periut.retroapi.registry.RetroRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Block.Sounds;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;

public class RetroBlock {
	private static int nextPlaceholderId = 200;

	private final RetroIdentifier id;
	private Material material = Material.STONE;
	private float hardness = 1.5f;
	private float resistance = 10.0f;
	private Block.Sounds sounds = Block.STONE_SOUNDS;
	private String translationKey;
	private float light = 0.0f;
	private int opacity = -1;
	private String texturePath;

	private RetroBlock(RetroIdentifier id) {
		this.id = id;
		this.translationKey = id.path();
		this.texturePath = id.path();
	}

	public static RetroBlock create(RetroIdentifier id) {
		return new RetroBlock(id);
	}

	public RetroBlock material(Material material) {
		this.material = material;
		return this;
	}

	public RetroBlock hardness(float hardness) {
		this.hardness = hardness;
		return this;
	}

	public RetroBlock resistance(float resistance) {
		this.resistance = resistance;
		return this;
	}

	public RetroBlock sounds(Block.Sounds sounds) {
		this.sounds = sounds;
		return this;
	}

	public RetroBlock translationKey(String translationKey) {
		this.translationKey = translationKey;
		return this;
	}

	public RetroBlock light(float light) {
		this.light = light;
		return this;
	}

	public RetroBlock opacity(int opacity) {
		this.opacity = opacity;
		return this;
	}

	public RetroBlock texturePath(String texturePath) {
		this.texturePath = texturePath;
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

	public Block build() {
		int placeholderId = allocatePlaceholderBlockId();

		RetroBlockImpl block = new RetroBlockImpl(placeholderId, material);
		block.setSounds(sounds);
		block.setStrength(hardness);
		block.setBlastResistance(resistance);
		block.setKey(translationKey);
		if (light > 0.0f) {
			block.setLight(light);
		}
		if (opacity >= 0) {
			block.setOpacity(opacity);
		}

		// Create BlockItem so ItemStack(Block) works (vanilla expects Item.BY_ID[block.id] to exist)
		// BlockItem(rawId) -> Item(rawId) -> id = rawId + 256, BY_ID[rawId + 256] = this
		// For block.id = placeholderId, we need rawId + 256 = placeholderId, so rawId = placeholderId - 256
		BlockItem blockItem = new BlockItem(placeholderId - 256);

		RetroRegistry.registerBlock(new BlockRegistration(id, block, blockItem, id.namespace(), texturePath));
		return block;
	}
}
