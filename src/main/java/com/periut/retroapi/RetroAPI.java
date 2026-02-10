package com.periut.retroapi;

import com.periut.retroapi.api.event.BlockRegistrationCallback;
import com.periut.retroapi.api.event.ItemRegistrationCallback;
import com.periut.retroapi.lang.LangLoader;
import com.periut.retroapi.mixin.WorldStorageAccessor;
import com.periut.retroapi.registry.IdAssigner;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.AlphaWorldStorage;
import net.minecraft.world.storage.WorldStorage;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class RetroAPI implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("RetroAPI");

	@Override
	public void init() {
		LOGGER.info("RetroAPI initializing");

		// Fire registration events so mods can register their blocks/items
		BlockRegistrationCallback.EVENT.invoker().run();
		ItemRegistrationCallback.EVENT.invoker().run();

		LOGGER.info("Registered {} blocks and {} items",
			RetroRegistry.getBlocks().size(), RetroRegistry.getItems().size());

		boolean hasStationAPI = FabricLoader.getInstance().isModLoaded("stationapi");

		if (!hasStationAPI) {
			// Register world load hooks for ID assignment (PREPARE_WORLD fires after save is loaded)
			MinecraftClientEvents.PREPARE_WORLD.register(this::onClientLoadWorld);

			// Register lang loading on client ready
			MinecraftClientEvents.READY.register(minecraft -> LangLoader.loadTranslations());
		} else {
			LOGGER.info("StationAPI detected - delegating ID management and textures to StationAPI");
		}
	}

	private void onClientLoadWorld(Minecraft minecraft) {
		if (minecraft.world == null) {
			return;
		}

		WorldStorage storage = minecraft.world.getStorage();
		File worldDir;
		if (storage instanceof AlphaWorldStorage) {
			worldDir = ((WorldStorageAccessor) storage).retroapi$getDir();
		} else {
			// Fallback: use getDataFile to determine directory
			worldDir = storage.getDataFile("retroapi_probe").getParentFile().getParentFile();
		}

		LOGGER.info("Loading world from: {}", worldDir);
		IdAssigner.assignIds(worldDir);
	}
}
