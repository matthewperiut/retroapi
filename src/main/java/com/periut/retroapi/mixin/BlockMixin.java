package com.periut.retroapi.mixin;

import com.periut.retroapi.api.*;
import com.periut.retroapi.compat.StationAPICompat;
import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin implements RetroBlockAccess {

	@Shadow public int id;
	@Shadow public int sprite;

	@Unique private int retroapi$renderType = -1;
	@Unique private boolean retroapi$solidRenderSet = false;
	@Unique private boolean retroapi$solidRender = true;
	@Unique private float[] retroapi$customBounds = null;
	@Unique private RetroBlockEntityType<?> retroapi$blockEntityType = null;
	@Unique private BlockActivatedHandler retroapi$activatedHandler = null;

	@Override
	public RetroBlockAccess retroapi$setSolidRender(boolean solid) {
		this.retroapi$solidRenderSet = true;
		this.retroapi$solidRender = solid;
		Block.IS_SOLID_RENDER[this.id] = solid;
		Block.OPACITIES[this.id] = solid ? 255 : 0;
		return this;
	}

	@Override
	public RetroBlockAccess retroapi$setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.retroapi$customBounds = new float[]{minX, minY, minZ, maxX, maxY, maxZ};
		((Block) (Object) this).setShape(minX, minY, minZ, maxX, maxY, maxZ);
		return this;
	}

	@Override
	public RetroBlockAccess setRenderType(RetroIdentifier renderTypeId) {
		this.retroapi$renderType = RenderType.resolve(renderTypeId);
		return this;
	}

	@Override
	public void retroapi$setSprite(int spriteId) {
		this.sprite = spriteId;
	}

	@Override
	public RetroBlockAccess retroapi$texture(RetroIdentifier textureId) {
		RetroTexture tex = RetroTextures.addBlockTexture(textureId);
		this.sprite = tex.id;
		RetroTextures.trackBlock((Block) (Object) this, tex);
		return this;
	}

	@Override
	public Block register(RetroIdentifier id) {
		Block self = (Block) (Object) this;
		boolean hasStationAPI = FabricLoader.getInstance().isModLoaded("stationapi");

		BlockItem blockItem = null;
		if (!hasStationAPI) {
			blockItem = new BlockItem(this.id - 256);
		}

		RetroRegistry.registerBlock(new BlockRegistration(id, self, blockItem));

		if (hasStationAPI) {
			StationAPICompat.registerBlock(id.namespace(), id.path(), self);
		}

		return self;
	}

	@Inject(method = "isSolidRender", at = @At("HEAD"), cancellable = true)
	private void retroapi$isSolidRender(CallbackInfoReturnable<Boolean> cir) {
		if (this.retroapi$solidRenderSet) {
			cir.setReturnValue(this.retroapi$solidRender);
		}
	}

	@Inject(method = "isCube", at = @At("HEAD"), cancellable = true)
	private void retroapi$isCube(CallbackInfoReturnable<Boolean> cir) {
		if (this.retroapi$solidRenderSet) {
			cir.setReturnValue(this.retroapi$solidRender);
		}
	}

	@Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
	private void retroapi$updateShape(WorldView world, int x, int y, int z, CallbackInfo ci) {
		if (this.retroapi$customBounds != null) {
			((Block) (Object) this).setShape(
				retroapi$customBounds[0], retroapi$customBounds[1], retroapi$customBounds[2],
				retroapi$customBounds[3], retroapi$customBounds[4], retroapi$customBounds[5]
			);
			ci.cancel();
		}
	}

	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true, require = 0)
	private void retroapi$getRenderType(CallbackInfoReturnable<Integer> cir) {
		if (this.retroapi$renderType != -1) {
			cir.setReturnValue(this.retroapi$renderType);
		}
	}

	@Override
	public RetroBlockAccess setBlockEntity(RetroBlockEntityType<?> type) {
		this.retroapi$blockEntityType = type;
		Block.HAS_BLOCK_ENTITY[this.id] = true;
		return this;
	}

	@Override
	public RetroBlockAccess setActivated(BlockActivatedHandler handler) {
		this.retroapi$activatedHandler = handler;
		return this;
	}

	@Inject(method = "onAdded", at = @At("HEAD"))
	private void retroapi$onAdded(World world, int x, int y, int z, CallbackInfo ci) {
		if (this.retroapi$blockEntityType != null) {
			world.setBlockEntity(x, y, z, this.retroapi$blockEntityType.create());
		}
	}

	@Inject(method = "onRemoved", at = @At("HEAD"))
	private void retroapi$onRemoved(World world, int x, int y, int z, CallbackInfo ci) {
		if (this.retroapi$blockEntityType != null) {
			world.removeBlockEntity(x, y, z);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void retroapi$use(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		if (this.retroapi$activatedHandler != null) {
			// Match vanilla BlockWithBlockEntity behavior: on client in multiplayer,
			// just return true without running the handler. The server will run the
			// handler and send inventory sync packets.
			if (world.isMultiplayer) {
				cir.setReturnValue(true);
				return;
			}
			cir.setReturnValue(this.retroapi$activatedHandler.onActivated(world, x, y, z, player));
		}
	}
}
