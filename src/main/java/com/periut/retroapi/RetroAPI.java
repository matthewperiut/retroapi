package com.periut.retroapi;

import com.periut.retroapi.api.event.BlockRegistrationCallback;
import com.periut.retroapi.api.event.ItemRegistrationCallback;
import com.periut.retroapi.compat.StationAPICompat;
import com.periut.retroapi.lang.LangLoader;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetroAPI implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("RetroAPI");

	@Override
	public void init() {
		LOGGER.info("RetroAPI initializing");

		boolean hasStationAPI = FabricLoader.getInstance().isModLoaded("stationapi");

		if (!hasStationAPI) {
			// Fire registration events so mods can register their blocks/items
			BlockRegistrationCallback.EVENT.invoker().run();
			ItemRegistrationCallback.EVENT.invoker().run();

			LOGGER.info("Registered {} blocks and {} items",
				RetroRegistry.getBlocks().size(), RetroRegistry.getItems().size());

			// Register lang loading on client ready
			MinecraftClientEvents.READY.register(minecraft -> LangLoader.loadTranslations());
		} else {
			// When StationAPI is present, it handles ID management and textures.
			// Register our lang path so StationAPI scans retroapi/lang/ directories.
			StationAPICompat.registerLangPath();
			LOGGER.info("StationAPI detected - delegating registration, ID management, and textures to StationAPI");
		}
	}
}
