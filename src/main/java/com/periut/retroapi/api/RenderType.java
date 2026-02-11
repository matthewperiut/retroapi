package com.periut.retroapi.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for block render types using flattened identifiers.
 * <p>
 * Vanilla render types are pre-registered with their fixed numeric IDs.
 * Custom render types are assigned IDs starting at 18.
 * <p>
 * Usage:
 * <pre>
 * // Use a vanilla render type
 * block.setRenderType(RenderType.STAIRS); // resolves to 10
 *
 * // Register and use a custom render type
 * RetroIdentifier SLOPE = RenderType.register(
 *     new RetroIdentifier("mymod", "slope"),
 *     ctx -> {
 *         // custom rendering logic using ctx helpers
 *         return true;
 *     }
 * );
 * block.setRenderType(SLOPE);
 * </pre>
 */
public final class RenderType {
	// Vanilla render type identifiers
	public static final RetroIdentifier BLOCK = new RetroIdentifier("minecraft", "block");
	public static final RetroIdentifier CROSS = new RetroIdentifier("minecraft", "cross");
	public static final RetroIdentifier TORCH = new RetroIdentifier("minecraft", "torch");
	public static final RetroIdentifier FIRE = new RetroIdentifier("minecraft", "fire");
	public static final RetroIdentifier LIQUID = new RetroIdentifier("minecraft", "liquid");
	public static final RetroIdentifier REDSTONE_WIRE = new RetroIdentifier("minecraft", "redstone_wire");
	public static final RetroIdentifier PLANT = new RetroIdentifier("minecraft", "plant");
	public static final RetroIdentifier DOOR = new RetroIdentifier("minecraft", "door");
	public static final RetroIdentifier LADDER = new RetroIdentifier("minecraft", "ladder");
	public static final RetroIdentifier RAIL = new RetroIdentifier("minecraft", "rail");
	public static final RetroIdentifier STAIRS = new RetroIdentifier("minecraft", "stairs");
	public static final RetroIdentifier FENCE = new RetroIdentifier("minecraft", "fence");
	public static final RetroIdentifier LEVER = new RetroIdentifier("minecraft", "lever");
	public static final RetroIdentifier CACTUS = new RetroIdentifier("minecraft", "cactus");
	public static final RetroIdentifier BED = new RetroIdentifier("minecraft", "bed");
	public static final RetroIdentifier REPEATER = new RetroIdentifier("minecraft", "repeater");
	public static final RetroIdentifier PISTON_BASE = new RetroIdentifier("minecraft", "piston_base");
	public static final RetroIdentifier PISTON_HEAD = new RetroIdentifier("minecraft", "piston_head");

	private static final Map<RetroIdentifier, Integer> idMap = new LinkedHashMap<>();
	private static final Map<Integer, CustomBlockRenderer> renderers = new LinkedHashMap<>();
	private static int nextId = 18;

	static {
		idMap.put(BLOCK, 0);
		idMap.put(CROSS, 1);
		idMap.put(TORCH, 2);
		idMap.put(FIRE, 3);
		idMap.put(LIQUID, 4);
		idMap.put(REDSTONE_WIRE, 5);
		idMap.put(PLANT, 6);
		idMap.put(DOOR, 7);
		idMap.put(LADDER, 8);
		idMap.put(RAIL, 9);
		idMap.put(STAIRS, 10);
		idMap.put(FENCE, 11);
		idMap.put(LEVER, 12);
		idMap.put(CACTUS, 13);
		idMap.put(BED, 14);
		idMap.put(REPEATER, 15);
		idMap.put(PISTON_BASE, 16);
		idMap.put(PISTON_HEAD, 17);
	}

	private RenderType() {}

	/**
	 * Register a custom render type with a renderer implementation.
	 *
	 * @param id       unique identifier for the render type
	 * @param renderer the rendering implementation
	 * @return the identifier (for use with {@link RetroBlock#setRenderType})
	 */
	public static RetroIdentifier register(RetroIdentifier id, CustomBlockRenderer renderer) {
		if (idMap.containsKey(id)) {
			throw new IllegalArgumentException("Render type already registered: " + id);
		}
		int numericId = nextId++;
		idMap.put(id, numericId);
		renderers.put(numericId, renderer);
		return id;
	}

	/**
	 * Resolve a render type identifier to its numeric ID.
	 * The result can be returned from {@code Block.getRenderType()}.
	 */
	public static int resolve(RetroIdentifier id) {
		Integer numericId = idMap.get(id);
		if (numericId == null) {
			throw new IllegalArgumentException("Unknown render type: " + id);
		}
		return numericId;
	}

	public static CustomBlockRenderer getRenderer(int numericId) {
		return renderers.get(numericId);
	}

	public static boolean isCustom(int numericId) {
		return numericId >= 18;
	}
}
