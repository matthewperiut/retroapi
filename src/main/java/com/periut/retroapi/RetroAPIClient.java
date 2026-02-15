package com.periut.retroapi;

import com.periut.retroapi.api.RetroMenu;
import com.periut.retroapi.lang.LangLoader;
import com.periut.retroapi.registry.IdAssigner;
import com.periut.retroapi.storage.ChunkExtendedBlocks;
import com.periut.retroapi.storage.ExtendedBlocksAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.chunk.WorldChunk;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

public class RetroAPIClient implements ClientModInitializer {
	@Override
	public void initClient() {
		RetroMenu.setClientOpener((player, menu, menuType) -> {
			Object be = menu.getBlockEntity();
			switch (menuType) {
				case RetroMenu.MENU_CHEST -> {
					if (be instanceof Inventory inv) {
						player.openChestMenu(inv);
					}
				}
				case RetroMenu.MENU_FURNACE -> {
					if (be instanceof Inventory inv) {
						FurnaceBlockEntityProxy proxy = new FurnaceBlockEntityProxy(inv, menu);
						player.openFurnaceMenu(proxy);
					}
				}
				case RetroMenu.MENU_DISPENSER -> {
					if (be instanceof Inventory inv) {
						player.openChestMenu(inv);
					}
				}
			}
		});

		boolean hasStationAPI = FabricLoader.getInstance().isModLoaded("stationapi");

		MinecraftClientEvents.READY.register(minecraft -> {
			LangLoader.loadTranslations();
		});

		if (!hasStationAPI) {

			ClientPlayNetworking.registerListener(RetroAPINetworking.ID_SYNC_CHANNEL, (ctx, buffer) -> {
				ctx.ensureOnMainThread();
				RetroAPI.LOGGER.info("Received ID sync packet from server");
				IdAssigner.applyFromNetwork(buffer);
			});

			ClientPlayNetworking.registerListener(RetroAPINetworking.CHUNK_EXTENDED_CHANNEL, (ctx, buffer) -> {
				ctx.ensureOnMainThread();

				int chunkX = buffer.readInt();
				int chunkZ = buffer.readInt();
				int count = buffer.readVarInt();

				Minecraft mc = ctx.minecraft();
				if (mc.world == null) return;

				WorldChunk chunk = mc.world.getChunkAt(chunkX, chunkZ);
				if (chunk == null) return;

				ChunkExtendedBlocks extended = ((ExtendedBlocksAccess) chunk).retroapi$getExtendedBlocks();
				for (int i = 0; i < count; i++) {
					int index = buffer.readVarInt();
					int blockId = buffer.readVarInt();
					int meta = buffer.readByte() & 0xFF;
					extended.set(index, blockId, meta);
				}

				if (mc.worldRenderer != null) {
					mc.worldRenderer.markDirty(
						chunkX * 16, 0, chunkZ * 16,
						chunkX * 16 + 15, 127, chunkZ * 16 + 15
					);
				}
			});
		}
	}
}
