package com.periut.retroapi.client;

import com.periut.retroapi.RetroAPI;
import com.periut.retroapi.network.RetroAPINetworking;
import com.periut.retroapi.lang.LangLoader;
import com.periut.retroapi.register.blockentity.MenuHelper;
import com.periut.retroapi.registry.IdAssigner;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.Inventory;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

public class RetroAPIClient implements ClientModInitializer {
	@Override
	public void initClient() {
		boolean hasStationAPI = FabricLoader.getInstance().isModLoaded("stationapi");

		MinecraftClientEvents.READY.register(minecraft -> {
			LangLoader.loadTranslations();
		});

		MenuHelper.setClientScreenOpener((player, inventory, screenId) -> {
			MenuHelper.ScreenEntry entry = MenuHelper.getScreenEntry(screenId.toString());
			if (entry == null) return;
			Object screen = entry.screenFactory().apply(player.inventory, inventory);
			if (screen instanceof InventoryMenuScreen menuScreen) {
				player.menu = menuScreen.menu;
				net.minecraft.client.Minecraft.INSTANCE.openScreen(menuScreen);
			}
		});

		ClientPlayNetworking.registerListener(RetroAPINetworking.OPEN_SCREEN_CHANNEL, (ctx, buffer) -> {
			ctx.ensureOnMainThread();
			String screenIdStr = buffer.readString();
			int menuId = buffer.readInt();

			MenuHelper.ScreenEntry entry = MenuHelper.getScreenEntry(screenIdStr);
			if (entry == null) {
				RetroAPI.LOGGER.warn("Unknown screen ID: {}", screenIdStr);
				return;
			}

			Inventory dummyInv = entry.inventoryFactory().get();
			net.minecraft.client.Minecraft minecraft = ctx.minecraft();
			net.minecraft.entity.mob.player.PlayerEntity player = minecraft.player;

			Object screen = entry.screenFactory().apply(player.inventory, dummyInv);
			if (screen instanceof InventoryMenuScreen menuScreen) {
				menuScreen.menu.networkId = menuId;
				player.menu = menuScreen.menu;
				minecraft.openScreen(menuScreen);
			}
		});

		if (!hasStationAPI) {
			ClientPlayNetworking.registerListener(RetroAPINetworking.ID_SYNC_CHANNEL, (ctx, buffer) -> {
				ctx.ensureOnMainThread();
				RetroAPI.LOGGER.info("Received ID sync packet from server");
				IdAssigner.applyFromNetwork(buffer);
			});
		}
	}
}
