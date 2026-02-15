package com.periut.retroapi.storage;

import net.minecraft.world.chunk.WorldChunk;

public interface BlocksUpdatePacketAccess {
	short[] retroapi$getFullBlockIds();
	void retroapi$populateFullIds(WorldChunk chunk);
}
