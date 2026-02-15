package com.periut.retroapi.mixin;

import com.periut.retroapi.RetroAPINetworking;
import com.periut.retroapi.storage.ChunkExtendedBlocks;
import com.periut.retroapi.storage.ExtendedBlocksAccess;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.WorldChunkPacket;
import net.minecraft.server.entity.mob.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.world.chunk.WorldChunk;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerPlayNetworkHandler.class)
public class ChunkSendMixin {

	@Shadow public ServerPlayerEntity player;

	@Inject(method = "sendPacket", at = @At("RETURN"))
	private void retroapi$onSendPacket(Packet packet, CallbackInfo ci) {
		if (!(packet instanceof WorldChunkPacket chunkPacket)) return;

		int chunkX = chunkPacket.x >> 4;
		int chunkZ = chunkPacket.z >> 4;

		WorldChunk chunk = player.world.getChunkAt(chunkX, chunkZ);
		if (chunk == null) return;

		ChunkExtendedBlocks extended = ((ExtendedBlocksAccess) chunk).retroapi$getExtendedBlocks();
		if (extended.isEmpty()) return;

		Map<Integer, Integer> blockIds = extended.getBlockIds();
		Map<Integer, Integer> metadataMap = extended.getMetadataMap();

		ServerPlayNetworking.send(player, RetroAPINetworking.CHUNK_EXTENDED_CHANNEL, buffer -> {
			buffer.writeInt(chunkX);
			buffer.writeInt(chunkZ);
			buffer.writeVarInt(blockIds.size());
			for (Map.Entry<Integer, Integer> entry : blockIds.entrySet()) {
				int index = entry.getKey();
				int blockId = entry.getValue();
				int meta = metadataMap.getOrDefault(index, 0);
				buffer.writeVarInt(index);
				buffer.writeVarInt(blockId);
				buffer.writeByte(meta);
			}
		});
	}
}
