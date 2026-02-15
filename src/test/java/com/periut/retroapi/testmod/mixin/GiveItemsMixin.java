package com.periut.retroapi.testmod.mixin;

import com.periut.retroapi.testmod.TestMod;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.entity.mob.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class GiveItemsMixin {
	@Shadow
	public PlayerInventory inventory;

	@Unique
	private boolean retroapi_test$givenItems = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void retroapi_test$giveTestItems(CallbackInfo ci) {
		if (!retroapi_test$givenItems) {
			retroapi_test$givenItems = true;

			PlayerEntity self = (PlayerEntity) (Object) this;
			World world = self.world;
			if (world == null || world.isMultiplayer) return;

			if (TestMod.TEST_BLOCK != null) {
				inventory.addItem(new ItemStack(TestMod.TEST_BLOCK, 64));
				inventory.addItem(new ItemStack(TestMod.COLOR_BLOCK, 64));
				inventory.addItem(new ItemStack(TestMod.PIPE_BLOCK, 64));
			}
			if (TestMod.CRATE_BLOCK != null) {
				inventory.addItem(new ItemStack(TestMod.CRATE_BLOCK, 64));
			}
			if (TestMod.TEST_ITEM != null) {
				inventory.addItem(new ItemStack(TestMod.TEST_ITEM, 64));
			}

			TestMod.LOGGER.info("Gave test items to player");
		}
	}
}
