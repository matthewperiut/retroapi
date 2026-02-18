package com.periut.retroapi.register.blockentity;

import com.periut.retroapi.mixin.network.ServerPlayerEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.network.packet.OpenInventoryMenuPacket;
import net.minecraft.server.entity.mob.player.ServerPlayerEntity;

public class MenuHelper {
	public static final int CHEST = 0;
	public static final int FURNACE = 2;
	public static final int DISPENSER = 3;

	public static void open(PlayerEntity player, InventoryMenu menu, int menuType, Inventory inventory) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			openServer(player, menu, menuType, inventory);
		} else {
			if (player instanceof ServerPlayerEntity) {
				openServer(player, menu, menuType, inventory);
			} else {
				switch (menuType) {
					case CHEST -> player.openChestMenu(inventory);
					case FURNACE -> {
						if (inventory instanceof FurnaceBlockEntity fbe) {
							player.openFurnaceMenu(fbe);
						}
					}
					case DISPENSER -> {
						if (inventory instanceof DispenserBlockEntity dbe) {
							player.openDispenserMenu(dbe);
						}
					}
				}
			}
		}
	}

	private static void openServer(PlayerEntity player, InventoryMenu menu, int menuType, Inventory inventory) {
		ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
		ServerPlayerEntityAccessor accessor = (ServerPlayerEntityAccessor) serverPlayer;
		accessor.invokeIncrementSyncId();
		int menuId = accessor.getMenuId();

		String name = inventory.getInventoryName();
		int size = inventory.getSize();

		serverPlayer.networkHandler.sendPacket(
			new OpenInventoryMenuPacket(menuId, menuType, name, size)
		);

		serverPlayer.menu = menu;
		menu.networkId = menuId;
		menu.addListener(serverPlayer);
	}
}
