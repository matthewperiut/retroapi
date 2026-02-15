package com.periut.retroapi.mixin;

import com.periut.retroapi.texture.AtlasExpander;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemRendererMixin {

	@Unique
	private int retroapi$atlasSize = 256;

	// --- render() for 2D dropped items: set atlas size before UV calculation ---

	@Inject(
		method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getSprite()I")
	)
	private void retroapi$setAtlasSizeForRender(ItemEntity itemEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
		retroapi$atlasSize = itemEntity.item.id < 256 ? AtlasExpander.terrainAtlasSize : AtlasExpander.itemAtlasSize;
	}

	@ModifyConstant(
		method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V",
		constant = @Constant(floatValue = 256.0F)
	)
	private float retroapi$fixRenderDivisor(float original) {
		return (float) retroapi$atlasSize;
	}

	// --- renderGuiItem -> drawTexture: set atlas size before drawing ---

	@Inject(
		method = "renderGuiItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemRenderer;drawTexture(IIIIII)V")
	)
	private void retroapi$setAtlasSizeForGui(
		net.minecraft.client.render.TextRenderer textRenderer,
		net.minecraft.client.render.texture.TextureManager textureManager,
		int item, int metadata, int sprite, int x, int y,
		CallbackInfo ci
	) {
		retroapi$atlasSize = item < 256 ? AtlasExpander.terrainAtlasSize : AtlasExpander.itemAtlasSize;
	}

	@ModifyConstant(
		method = "drawTexture",
		constant = @Constant(floatValue = 0.00390625F)
	)
	private float retroapi$fixDrawTextureScale(float original) {
		if (retroapi$atlasSize != 256) {
			return 1.0F / retroapi$atlasSize;
		}
		return original;
	}
}
