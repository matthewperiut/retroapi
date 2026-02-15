package com.periut.retroapi.api;

import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.world.World;

@FunctionalInterface
public interface BlockActivatedHandler {
	boolean onActivated(World world, int x, int y, int z, PlayerEntity player);
}
