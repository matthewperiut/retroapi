package com.periut.retroapi.registry;

import com.periut.retroapi.api.RetroIdentifier;
import com.periut.retroapi.mixin.BlockAccessor;
import com.periut.retroapi.mixin.BlockItemAccessor;
import com.periut.retroapi.mixin.ItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ornithemc.osl.networking.api.PacketBuffer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class IdAssigner {
	private static final Logger LOGGER = LogManager.getLogger("RetroAPI/IdAssigner");

	public static void assignIds(File worldDir) {
		File idMapFile = new File(worldDir, "retroapi/id_map.dat");
		IdMap idMap = new IdMap();
		idMap.load(idMapFile);

		assignBlockIds(idMap);
		assignItemIds(idMap);

		idMap.save(idMapFile);
	}

	private static void assignBlockIds(IdMap idMap) {
		Block[] byId = Block.BY_ID;
		Set<Integer> usedIds = new HashSet<>();

		// Collect all currently used vanilla block IDs
		for (int i = 0; i < byId.length; i++) {
			if (byId[i] != null) {
				boolean isRetroBlock = false;
				for (BlockRegistration reg : RetroRegistry.getBlocks()) {
					if (reg.getBlock() == byId[i]) {
						isRetroBlock = true;
						break;
					}
				}
				if (!isRetroBlock) {
					usedIds.add(i);
				}
			}
		}

		// Also mark IDs already assigned in the map as used
		for (int id : idMap.getBlockIds().values()) {
			usedIds.add(id);
		}

		for (BlockRegistration reg : RetroRegistry.getBlocks()) {
			Block block = reg.getBlock();
			int currentId = ((BlockAccessor) block).retroapi$getId();
			Integer mappedId = idMap.getBlockId(reg.getId());

			int targetId;
			if (mappedId != null) {
				targetId = mappedId;
			} else {
				// Assign new ID
				targetId = findFreeBlockId(usedIds);
				idMap.putBlockId(reg.getId(), targetId);
				usedIds.add(targetId);
			}

			if (currentId != targetId) {
				remapBlock(block, currentId, targetId);
			}

			LOGGER.info("Assigned block {} -> ID {}", reg.getId(), targetId);
		}
	}

	private static void assignItemIds(IdMap idMap) {
		Item[] byId = Item.BY_ID;
		Set<Integer> usedIds = new HashSet<>();

		// Collect all currently used vanilla item IDs (raw, including the +256 offset)
		for (int i = 256; i < byId.length; i++) {
			if (byId[i] != null) {
				boolean isRetroItem = false;
				for (ItemRegistration reg : RetroRegistry.getItems()) {
					if (reg.getItem() == byId[i]) {
						isRetroItem = true;
						break;
					}
				}
				if (!isRetroItem) {
					usedIds.add(i);
				}
			}
		}

		for (int id : idMap.getItemIds().values()) {
			usedIds.add(id);
		}

		for (ItemRegistration reg : RetroRegistry.getItems()) {
			Item item = reg.getItem();
			int currentId = ((ItemAccessor) item).retroapi$getId();
			Integer mappedId = idMap.getItemId(reg.getId());

			int targetId;
			if (mappedId != null) {
				targetId = mappedId;
			} else {
				targetId = findFreeItemId(usedIds);
				idMap.putItemId(reg.getId(), targetId);
				usedIds.add(targetId);
			}

			if (currentId != targetId) {
				remapItem(item, currentId, targetId);
			}

			LOGGER.info("Assigned item {} -> ID {}", reg.getId(), targetId);
		}
	}

	private static int findFreeBlockId(Set<Integer> usedIds) {
		for (int i = 200; i < 256; i++) {
			if (!usedIds.contains(i)) {
				return i;
			}
		}
		// Fall back to lower range
		for (int i = 1; i < 200; i++) {
			if (!usedIds.contains(i)) {
				return i;
			}
		}
		throw new RuntimeException("No free block IDs available");
	}

	private static int findFreeItemId(Set<Integer> usedIds) {
		// Item IDs stored with +256 offset
		for (int i = 2256; i < 32000; i++) {
			if (!usedIds.contains(i)) {
				return i;
			}
		}
		for (int i = 256; i < 2256; i++) {
			if (!usedIds.contains(i)) {
				return i;
			}
		}
		throw new RuntimeException("No free item IDs available");
	}

	private static void remapBlock(Block block, int oldId, int newId) {
		Block[] byId = Block.BY_ID;

		// Save light value before clearing
		int lightValue = (oldId >= 0 && oldId < Block.LIGHT.length) ? Block.LIGHT[oldId] : 0;

		// Clear old slot
		if (oldId >= 0 && oldId < byId.length && byId[oldId] == block) {
			byId[oldId] = null;
			Block.IS_SOLID_RENDER[oldId] = false;
			Block.OPACITIES[oldId] = 0;
			Block.IS_TRANSLUCENT[oldId] = false;
			Block.HAS_BLOCK_ENTITY[oldId] = false;
			Block.TICKS_RANDOMLY[oldId] = false;
			Block.LIGHT[oldId] = 0;
			Block.UPDATE_CLIENTS[oldId] = false;
		}

		// Set new slot
		byId[newId] = block;
		Block.IS_SOLID_RENDER[newId] = block.isSolidRender();
		Block.OPACITIES[newId] = block.isSolidRender() ? 255 : 0;
		Block.IS_TRANSLUCENT[newId] = !block.material.isOpaque();
		Block.LIGHT[newId] = lightValue;

		// Update the block's id field
		((BlockAccessor) block).retroapi$setId(newId);

		// Remap the corresponding BlockItem in Item.BY_ID
		Item[] itemById = Item.BY_ID;
		if (oldId >= 0 && oldId < itemById.length && itemById[oldId] instanceof BlockItem) {
			BlockItem blockItem = (BlockItem) itemById[oldId];
			itemById[oldId] = null;
			itemById[newId] = blockItem;
			((ItemAccessor) blockItem).retroapi$setId(newId);
			((BlockItemAccessor) blockItem).retroapi$setBlock(newId);
		}

		LOGGER.debug("Remapped block from {} to {}", oldId, newId);
	}

	private static void remapItem(Item item, int oldId, int newId) {
		Item[] byId = Item.BY_ID;

		// Clear old slot
		if (oldId >= 0 && oldId < byId.length && byId[oldId] == item) {
			byId[oldId] = null;
		}

		// Set new slot
		byId[newId] = item;

		// Update the item's id field
		((ItemAccessor) item).retroapi$setId(newId);

		LOGGER.debug("Remapped item from {} to {}", oldId, newId);
	}

	public static void applyFromNetwork(PacketBuffer buffer) {
		int blockCount = buffer.readVarInt();
		for (int i = 0; i < blockCount; i++) {
			String identifier = buffer.readString();
			int numericId = buffer.readVarInt();

			String[] parts = identifier.split(":", 2);
			if (parts.length != 2) {
				LOGGER.warn("Invalid block identifier from server: {}", identifier);
				continue;
			}
			RetroIdentifier retroId = new RetroIdentifier(parts[0], parts[1]);
			BlockRegistration reg = RetroRegistry.getBlockById(retroId);
			if (reg == null) {
				LOGGER.warn("Server has unknown block: {} (id {}), skipping", identifier, numericId);
				continue;
			}

			Block block = reg.getBlock();
			int currentId = ((BlockAccessor) block).retroapi$getId();
			if (currentId != numericId) {
				remapBlock(block, currentId, numericId);
				LOGGER.info("Synced block {} -> ID {} (was {})", identifier, numericId, currentId);
			}
		}

		int itemCount = buffer.readVarInt();
		for (int i = 0; i < itemCount; i++) {
			String identifier = buffer.readString();
			int numericId = buffer.readVarInt();

			String[] parts = identifier.split(":", 2);
			if (parts.length != 2) {
				LOGGER.warn("Invalid item identifier from server: {}", identifier);
				continue;
			}
			RetroIdentifier retroId = new RetroIdentifier(parts[0], parts[1]);
			ItemRegistration reg = RetroRegistry.getItemById(retroId);
			if (reg == null) {
				LOGGER.warn("Server has unknown item: {} (id {}), skipping", identifier, numericId);
				continue;
			}

			Item item = reg.getItem();
			int currentId = ((ItemAccessor) item).retroapi$getId();
			if (currentId != numericId) {
				remapItem(item, currentId, numericId);
				LOGGER.info("Synced item {} -> ID {} (was {})", identifier, numericId, currentId);
			}
		}
	}
}
