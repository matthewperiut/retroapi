package com.periut.retroapi.mixin;

import com.periut.retroapi.registry.IdAssigner;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.storage.AlphaWorldStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(AlphaWorldStorage.class)
public class AlphaWorldStorageMixin {
	private static final Logger LOGGER = LogManager.getLogger("RetroAPI");

	@Inject(method = "<init>", at = @At("TAIL"))
	private void retroapi$assignIds(CallbackInfo ci) {
		if (FabricLoader.getInstance().isModLoaded("stationapi")) return;
		if (RetroRegistry.getBlocks().isEmpty() && RetroRegistry.getItems().isEmpty()) return;

		File worldDir = ((WorldStorageAccessor) (Object) this).retroapi$getDir();
		LOGGER.info("Assigning IDs for world: {}", worldDir);
		IdAssigner.assignIds(worldDir);
	}
}
