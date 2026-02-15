package com.periut.retroapi.mixin;

import com.periut.retroapi.texture.AtlasExpander;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.ItemInHandRenderer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemInHandRendererMixin {

	@Unique
	private int retroapi$atlasSize = 256;

	// --- render(): set atlas size before UV calculation for held 2D items ---

	@Inject(
		method = "render(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/item/ItemStack;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getItemSprite(Lnet/minecraft/item/ItemStack;)I")
	)
	private void retroapi$setAtlasSizeForRender(MobEntity mob, ItemStack itemInHand, CallbackInfo ci) {
		retroapi$atlasSize = itemInHand.id < 256 ? AtlasExpander.terrainAtlasSize : 256;
	}

	@ModifyConstant(
		method = "render(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/item/ItemStack;)V",
		constant = @Constant(floatValue = 256.0F)
	)
	private float retroapi$fixRenderDivisor(float original) {
		return (float) retroapi$atlasSize;
	}

	@ModifyConstant(
		method = "render(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/item/ItemStack;)V",
		constant = @Constant(floatValue = 0.001953125F)
	)
	private float retroapi$fixEdgeBias(float original) {
		return 0.5F / retroapi$atlasSize;
	}

	// --- renderInWallEffect: always terrain atlas ---

	@Inject(method = "renderInWallEffect", at = @At("HEAD"))
	private void retroapi$setTerrainAtlasForWall(float tickDelta, int sprite, CallbackInfo ci) {
		retroapi$atlasSize = AtlasExpander.terrainAtlasSize;
	}

	@ModifyConstant(
		method = "renderInWallEffect",
		constant = @Constant(floatValue = 256.0F)
	)
	private float retroapi$fixWallEffectDivisor(float original) {
		return (float) retroapi$atlasSize;
	}

	// --- renderOnFireEffect: always terrain atlas, also needs row mask fix ---

	@Inject(method = "renderOnFireEffect", at = @At("HEAD"))
	private void retroapi$setTerrainAtlasForFire(float tickDelta, CallbackInfo ci) {
		retroapi$atlasSize = AtlasExpander.terrainAtlasSize;
	}

	@ModifyConstant(
		method = "renderOnFireEffect",
		constant = @Constant(intValue = 240)
	)
	private int retroapi$fixFireRowMask(int original) {
		return -16;
	}

	@ModifyConstant(
		method = "renderOnFireEffect",
		constant = @Constant(floatValue = 256.0F)
	)
	private float retroapi$fixFireEffectDivisor(float original) {
		return (float) retroapi$atlasSize;
	}
}
