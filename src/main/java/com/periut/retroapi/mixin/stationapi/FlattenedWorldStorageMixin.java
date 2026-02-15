package com.periut.retroapi.mixin.stationapi;

import com.periut.retroapi.compat.WorldConversionHelper;
import com.periut.retroapi.mixin.RegionWorldStorageSourceAccessor;
import com.periut.retroapi.storage.BackupManager;
import net.minecraft.util.ProgressListener;
import net.modificationstation.stationapi.impl.world.storage.FlattenedWorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.function.BiFunction;

@Mixin(FlattenedWorldStorage.class)
public abstract class FlattenedWorldStorageMixin {

	@Inject(method = "convertWorld", at = @At("HEAD"), remap = false)
	private void retroapi$beforeConvertWorld(String worldFolder, BiFunction<?, ?, ?> convertFunction, ProgressListener progress, CallbackInfoReturnable<Boolean> cir) {
		File savesDir = ((RegionWorldStorageSourceAccessor) this).retroapi$getDir();
		File worldDir = new File(savesDir, worldFolder);
		BackupManager.backupRetroApiData(worldDir);
		WorldConversionHelper.injectRetroApiMappings(worldDir);
	}
}
