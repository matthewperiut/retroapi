package com.periut.retroapi.registry;

import com.periut.retroapi.register.RetroIdentifier;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockRegistration {
	private final RetroIdentifier id;
	private final Block block;
	private final BlockItem blockItem;
	private final int placeholderId;

	public BlockRegistration(RetroIdentifier id, Block block, BlockItem blockItem) {
		this.id = id;
		this.block = block;
		this.blockItem = blockItem;
		this.placeholderId = block.id;
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

	public int getPlaceholderId() {
		return placeholderId;
	}
}
