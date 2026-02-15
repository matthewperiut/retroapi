package com.periut.retroapi.mixin;

import com.periut.retroapi.api.RetroBlockAccess;
import com.periut.retroapi.registry.RetroRegistry;
import com.periut.retroapi.storage.ChunkExtendedBlocks;
import com.periut.retroapi.storage.ExtendedBlocksAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;

@Mixin(WorldChunk.class)
public class WorldChunkMixin implements ExtendedBlocksAccess {

	@Shadow public byte[] blocks;
	@Shadow public World world;
	@Shadow public int chunkX;
	@Shadow public int chunkZ;
	@Shadow public boolean dirty;
	@Shadow public Map<BlockPos, BlockEntity> blockEntities;

	@Unique
	private final ChunkExtendedBlocks retroapi$extendedBlocks = new ChunkExtendedBlocks();

	@Unique
	private boolean retroapi$handlingExtended = false;

	@Override
	public ChunkExtendedBlocks retroapi$getExtendedBlocks() {
		return retroapi$extendedBlocks;
	}

	@Inject(method = "getBlockAt", at = @At("RETURN"), cancellable = true)
	private void retroapi$getBlockAt(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
		int index = ChunkExtendedBlocks.toIndex(x, y, z);
		if (retroapi$extendedBlocks.hasEntry(index)) {
			cir.setReturnValue(retroapi$extendedBlocks.getBlockId(index));
		}
	}

	@Inject(method = "setBlockAt(IIII)Z", at = @At("HEAD"), cancellable = true)
	private void retroapi$handleSetBlock(int x, int y, int z, int rawId, CallbackInfoReturnable<Boolean> cir) {
		if (retroapi$handlingExtended) return;

		int index = ChunkExtendedBlocks.toIndex(x, y, z);

		if (rawId >= 256) {
			// Handle removal of old extended block at this position
			if (retroapi$extendedBlocks.hasEntry(index)) {
				int oldExtId = retroapi$extendedBlocks.getBlockId(index);
				retroapi$extendedBlocks.remove(index);
				Block oldBlock = (oldExtId >= 0 && oldExtId < Block.BY_ID.length) ? Block.BY_ID[oldExtId] : null;
				if (oldBlock != null && world != null) {
					oldBlock.onRemoved(world, chunkX * 16 + x, y, chunkZ * 16 + z);
				}
			}

			// Let vanilla handle clearing the byte array position (heightmap, lighting, vanilla block removal)
			retroapi$handlingExtended = true;
			((WorldChunk) (Object) this).setBlockAt(x, y, z, 0);
			retroapi$handlingExtended = false;

			// Store the extended block
			retroapi$extendedBlocks.set(index, rawId, 0);

			// Call onAdded for the new block
			Block newBlock = Block.BY_ID[rawId];
			if (newBlock != null && world != null && !world.isMultiplayer) {
				newBlock.onAdded(world, chunkX * 16 + x, y, chunkZ * 16 + z);
			}

			dirty = true;
			cir.setReturnValue(true);
		} else {
			// Vanilla block being placed - clear any extended entry
			if (retroapi$extendedBlocks.hasEntry(index)) {
				int oldExtId = retroapi$extendedBlocks.getBlockId(index);
				retroapi$extendedBlocks.remove(index);
				Block oldBlock = (oldExtId >= 0 && oldExtId < Block.BY_ID.length) ? Block.BY_ID[oldExtId] : null;
				if (oldBlock != null && world != null) {
					oldBlock.onRemoved(world, chunkX * 16 + x, y, chunkZ * 16 + z);
				}
			}
			// Let vanilla handle the rest
		}
	}

	@Inject(method = "setBlockWithMetadataAt", at = @At("HEAD"), cancellable = true)
	private void retroapi$handleSetBlockWithMeta(int x, int y, int z, int rawId, int meta, CallbackInfoReturnable<Boolean> cir) {
		if (retroapi$handlingExtended) return;

		int index = ChunkExtendedBlocks.toIndex(x, y, z);

		if (rawId >= 256) {
			// Handle removal of old extended block at this position
			if (retroapi$extendedBlocks.hasEntry(index)) {
				int oldExtId = retroapi$extendedBlocks.getBlockId(index);
				retroapi$extendedBlocks.remove(index);
				Block oldBlock = (oldExtId >= 0 && oldExtId < Block.BY_ID.length) ? Block.BY_ID[oldExtId] : null;
				if (oldBlock != null && world != null && !world.isMultiplayer) {
					oldBlock.onRemoved(world, chunkX * 16 + x, y, chunkZ * 16 + z);
				}
			}

			// Let vanilla clear the byte array position
			retroapi$handlingExtended = true;
			((WorldChunk) (Object) this).setBlockWithMetadataAt(x, y, z, 0, 0);
			retroapi$handlingExtended = false;

			// Store the extended block with metadata
			retroapi$extendedBlocks.set(index, rawId, meta);

			// Call onAdded for the new block
			Block newBlock = Block.BY_ID[rawId];
			if (newBlock != null && world != null) {
				newBlock.onAdded(world, chunkX * 16 + x, y, chunkZ * 16 + z);
			}

			dirty = true;
			cir.setReturnValue(true);
		} else {
			// Vanilla block being placed - clear any extended entry
			if (retroapi$extendedBlocks.hasEntry(index)) {
				int oldExtId = retroapi$extendedBlocks.getBlockId(index);
				retroapi$extendedBlocks.remove(index);
				Block oldBlock = (oldExtId >= 0 && oldExtId < Block.BY_ID.length) ? Block.BY_ID[oldExtId] : null;
				if (oldBlock != null && world != null && !world.isMultiplayer) {
					oldBlock.onRemoved(world, chunkX * 16 + x, y, chunkZ * 16 + z);
				}
			}
			// Let vanilla handle the rest
		}
	}

	@Inject(method = "setBlockEntityAt", at = @At("HEAD"), cancellable = true)
	private void retroapi$setBlockEntityAt(int x, int y, int z, BlockEntity be, CallbackInfo ci) {
		int blockId = ((WorldChunk) (Object) this).getBlockAt(x, y, z);
		if (blockId <= 0 || blockId >= Block.BY_ID.length) return;
		Block block = Block.BY_ID[blockId];
		if (block == null) return;

		// For non-BlockWithBlockEntity blocks that have HAS_BLOCK_ENTITY set (RetroAPI blocks),
		// bypass the vanilla instanceof check and store the BE directly
		if (!(block instanceof BlockWithBlockEntity) && Block.HAS_BLOCK_ENTITY[blockId]) {
			be.world = world;
			be.x = chunkX * 16 + x;
			be.y = y;
			be.z = chunkZ * 16 + z;
			be.cancelRemoval();
			blockEntities.put(new BlockPos(x, y, z), be);
			ci.cancel();
		}
	}

	@Inject(method = "getBlockEntityAt", at = @At("HEAD"), cancellable = true)
	private void retroapi$getBlockEntityAt(int x, int y, int z, CallbackInfoReturnable<BlockEntity> cir) {
		BlockPos pos = new BlockPos(x, y, z);
		BlockEntity existing = blockEntities.get(pos);
		if (existing != null) {
			if (existing.isRemoved()) {
				blockEntities.remove(pos);
				cir.setReturnValue(null);
			} else {
				cir.setReturnValue(existing);
			}
			return;
		}

		int blockId = ((WorldChunk) (Object) this).getBlockAt(x, y, z);
		if (blockId <= 0 || blockId >= Block.BY_ID.length) return;
		if (!Block.HAS_BLOCK_ENTITY[blockId]) return;
		Block block = Block.BY_ID[blockId];
		if (block == null) return;

		// Only intercept non-vanilla block entity blocks to avoid the ClassCastException
		if (!(block instanceof BlockWithBlockEntity)) {
			// RetroAPI block entity — create via our onAdded which sets the BE
			block.onAdded(world, chunkX * 16 + x, y, chunkZ * 16 + z);
			existing = blockEntities.get(pos);
			cir.setReturnValue(existing);
		}
	}

	@Unique
	private static boolean retroapi$isRetroApiBlock(int blockId) {
		if (blockId <= 0 || blockId >= Block.BY_ID.length) return false;
		Block block = Block.BY_ID[blockId];
		if (block == null) return false;
		return RetroRegistry.getBlockRegistration(block) != null;
	}
}
