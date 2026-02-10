package com.periut.retroapi.registry;

import com.periut.retroapi.api.RetroIdentifier;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockRegistration {
	private final RetroIdentifier id;
	private final Block block;
	private final BlockItem blockItem;
	private final String textureNamespace;
	private final String texturePath;

	public BlockRegistration(RetroIdentifier id, Block block, BlockItem blockItem, String textureNamespace, String texturePath) {
		this.id = id;
		this.block = block;
		this.blockItem = blockItem;
		this.textureNamespace = textureNamespace;
		this.texturePath = texturePath;
	}

	public RetroIdentifier getId() {
		return id;
	}

	public Block getBlock() {
		return block;
	}

	public BlockItem getBlockItem() {
		return blockItem;
	}

	public String getTextureNamespace() {
		return textureNamespace;
	}

	public String getTexturePath() {
		return texturePath;
	}
}
