package com.periut.retroapi.storage;

public interface WorldChunkPacketAccess {
	int retroapi$getExtCount();
	int[] retroapi$getExtIndices();
	int[] retroapi$getExtBlockIds();
	int[] retroapi$getExtMeta();
	void retroapi$populateExtended(ChunkExtendedBlocks extended);
}
