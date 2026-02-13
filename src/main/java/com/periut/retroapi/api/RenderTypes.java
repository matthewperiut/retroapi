package com.periut.retroapi.api;

/**
 * Constants for all vanilla block render types in Beta 1.7.3.
 * <p>
 * Use these with {@link RetroBlockAccess#retroapi$setRenderType} or pass to
 * {@link RenderType#resolve} to get the numeric ID.
 */
public final class RenderTypes {
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

	private RenderTypes() {}
}
