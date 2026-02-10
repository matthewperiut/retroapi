package com.periut.retroapi.compat;

import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.ItemRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = true))
public class StationAPIRegistryForwarder {
	private static final Logger LOGGER = LogManager.getLogger("RetroAPI/StationAPI");

	@Entrypoint.Namespace
	public Namespace namespace = Namespace.of("retroapi");

	@Entrypoint.Logger
	public Logger logger = LOGGER;

	public void registerBlocks(BlockRegistryEvent event) {
		for (BlockRegistration reg : RetroRegistry.getBlocks()) {
			Identifier id = Identifier.of(reg.getId().namespace() + ":" + reg.getId().path());
			event.register(id, reg.getBlock());
			LOGGER.info("Forwarded block {} to StationAPI registry", reg.getId());
		}
	}

	public void registerItems(ItemRegistryEvent event) {
		for (ItemRegistration reg : RetroRegistry.getItems()) {
			Identifier id = Identifier.of(reg.getId().namespace() + ":" + reg.getId().path());
			event.register(id, reg.getItem());
			LOGGER.info("Forwarded item {} to StationAPI registry", reg.getId());
		}
	}
}
