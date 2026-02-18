package com.periut.retroapi.register.blockentity;

import com.periut.retroapi.mixin.network.ServerPlayerEntityAccessor;
import com.periut.retroapi.network.RetroAPINetworking;
import com.periut.retroapi.register.RetroIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.entity.mob.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.network.packet.OpenInventoryMenuPacket;
import net.minecraft.server.entity.mob.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MenuHelper {
	public static final int CHEST = 0;
	public static final int FURNACE = 2;
	public static final int DISPENSER = 3;

	private static final Map<String, ScreenEntry> screenFactories = new HashMap<>();

	public record ScreenEntry(Supplier<Inventory> inventoryFactory,
							  BiFunction<PlayerInventory, Inventory, Object> screenFactory) {}

	public interface ClientScreenOpener {
		void open(PlayerEntity player, Inventory inventory, RetroIdentifier screenId);
	}

	private static ClientScreenOpener clientScreenOpener;

	public static void setClientScreenOpener(ClientScreenOpener opener) {
		clientScreenOpener = opener;
	}

	public static void registerScreen(RetroIdentifier id,
									  Supplier<Inventory> inventoryFactory,
									  BiFunction<PlayerInventory, Inventory, Object> screenFactory) {
		screenFactories.put(id.toString(), new ScreenEntry(inventoryFactory, screenFactory));
	}

	public static ScreenEntry getScreenEntry(String id) {
		return screenFactories.get(id);
	}

	public static void open(PlayerEntity player, InventoryMenu menu, int menuType, Inventory inventory) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
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

	public static void open(PlayerEntity player, InventoryMenu menu, int menuType,
							Inventory inventory, RetroIdentifier screenId) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			openServerCustomScreen(player, menu, inventory, screenId);
		} else {
			if (clientScreenOpener != null) {
				clientScreenOpener.open(player, inventory, screenId);
			}
		}
	}

	private static void openServerCustomScreen(PlayerEntity player, InventoryMenu menu,
											   Inventory inventory, RetroIdentifier screenId) {
		ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
		ServerPlayerEntityAccessor accessor = (ServerPlayerEntityAccessor) serverPlayer;
		accessor.invokeIncrementSyncId();
		int menuId = accessor.getMenuId();

		String screenIdStr = screenId.toString();
		ServerPlayNetworking.send(serverPlayer, RetroAPINetworking.OPEN_SCREEN_CHANNEL, buf -> {
			buf.writeString(screenIdStr);
			buf.writeInt(menuId);
		});

		serverPlayer.menu = menu;
		menu.networkId = menuId;
		menu.addListener(serverPlayer);
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
