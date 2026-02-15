package com.periut.retroapi.mixin;

import com.periut.retroapi.texture.AtlasExpander;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockRenderer.class)
@Environment(EnvType.CLIENT)
public class BlockRendererAtlasMixin {

	@ModifyConstant(
		method = {
			"tesselateBed", "tesselateRepeater",
			"tesselatePistonSideX", "tesselatePistonSideY", "tesselatePistonSideZ",
			"tesselateLever", "tesselateFire", "tesselateRedstoneWire",
			"tesselateRail", "tesselateLadder",
			"tesselateTorch(Lnet/minecraft/block/Block;DDDDD)V",
			"tesselateCross(Lnet/minecraft/block/Block;IDDD)V",
			"tesselatePlant(Lnet/minecraft/block/Block;IDDD)V",
			"tesselateLiquid",
			"tesselateBottomFace", "tesselateTopFace",
			"tesselateNorthFace", "tesselateSouthFace",
			"tesselateWestFace", "tesselateEastFace"
		},
		constant = @Constant(intValue = 240)
	)
	private int retroapi$fixRowMask(int original) {
		return -16;
	}

	@ModifyConstant(
		method = {
			"tesselateBed", "tesselateRepeater",
			"tesselatePistonSideX", "tesselatePistonSideY", "tesselatePistonSideZ",
			"tesselateLever", "tesselateFire", "tesselateRedstoneWire",
			"tesselateRail", "tesselateLadder",
			"tesselateTorch(Lnet/minecraft/block/Block;DDDDD)V",
			"tesselateCross(Lnet/minecraft/block/Block;IDDD)V",
			"tesselatePlant(Lnet/minecraft/block/Block;IDDD)V",
			"tesselateLiquid",
			"tesselateBottomFace", "tesselateTopFace",
			"tesselateNorthFace", "tesselateSouthFace",
			"tesselateWestFace", "tesselateEastFace"
		},
		constant = @Constant(doubleValue = 256.0)
	)
	private double retroapi$fixAtlasDivisorDouble(double original) {
		return AtlasExpander.terrainAtlasSize;
	}

	@ModifyConstant(
		method = {
			"tesselateBed", "tesselateRepeater",
			"tesselatePistonSideX", "tesselatePistonSideY", "tesselatePistonSideZ",
			"tesselateLever", "tesselateFire", "tesselateRedstoneWire",
			"tesselateRail", "tesselateLadder",
			"tesselateTorch(Lnet/minecraft/block/Block;DDDDD)V",
			"tesselateCross(Lnet/minecraft/block/Block;IDDD)V",
			"tesselatePlant(Lnet/minecraft/block/Block;IDDD)V",
			"tesselateLiquid",
			"tesselateBottomFace", "tesselateTopFace",
			"tesselateNorthFace", "tesselateSouthFace",
			"tesselateWestFace", "tesselateEastFace"
		},
		constant = @Constant(floatValue = 256.0F)
	)
	private float retroapi$fixAtlasDivisorFloat(float original) {
		return (float) AtlasExpander.terrainAtlasSize;
	}
}
