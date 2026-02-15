package com.periut.retroapi.mixin;

import com.periut.retroapi.storage.ChunkExtendedBlocks;
import com.periut.retroapi.storage.ExtendedBlocksAccess;
import com.periut.retroapi.storage.RegionSidecar;
import com.periut.retroapi.storage.SidecarManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AlphaChunkStorage.class)
public class ChunkStorageMixin {

	@Inject(method = "loadChunkFromNbt", at = @At("RETURN"))
	private static void retroapi$onLoadChunk(World world, NbtCompound nbt, CallbackInfoReturnable<WorldChunk> cir) {
		WorldChunk chunk = cir.getReturnValue();
		if (chunk == null) return;

		RegionSidecar sidecar = SidecarManager.getRegion(chunk.chunkX, chunk.chunkZ);
		if (sidecar == null) return;

		ChunkExtendedBlocks extended = ((ExtendedBlocksAccess) chunk).retroapi$getExtendedBlocks();
		sidecar.loadChunkData(chunk.chunkX, chunk.chunkZ, extended);
	}

	@Inject(method = "saveChunkToNbt", at = @At("HEAD"))
	private static void retroapi$sanitizeBeforeSave(WorldChunk chunk, World world, NbtCompound nbt, CallbackInfo ci) {
		ChunkExtendedBlocks extended = ((ExtendedBlocksAccess) chunk).retroapi$getExtendedBlocks();
		for (int index : extended.getBlockIds().keySet()) {
			int blockId = extended.getBlockId(index);
			if (blockId >= 256 && index >= 0 && index < chunk.blocks.length) {
				chunk.blocks[index] = 0;
			}
		}
	}

	@Inject(method = "saveChunkToNbt", at = @At("RETURN"))
	private static void retroapi$onSaveChunk(WorldChunk chunk, World world, NbtCompound nbt, CallbackInfo ci) {
		RegionSidecar sidecar = SidecarManager.getRegion(chunk.chunkX, chunk.chunkZ);
		if (sidecar == null) return;

		ChunkExtendedBlocks extended = ((ExtendedBlocksAccess) chunk).retroapi$getExtendedBlocks();
		sidecar.saveChunkData(chunk.chunkX, chunk.chunkZ, extended);
	}
}
