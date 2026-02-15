package com.periut.retroapi.mixin;

import com.periut.retroapi.api.RetroIdentifier;
import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.ItemRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Shadow public int id;
	@Shadow public int size;

	@Inject(method = "writeNbt", at = @At("RETURN"))
	private void retroapi$writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		String stringId = resolveStringId(this.id);
		if (stringId != null) {
			nbt.putString("retroapi:id", stringId);
			// Clamp the numeric id to 0 for block items with block id >= 256
			if (this.id >= 0 && this.id < Block.BY_ID.length && Block.BY_ID[this.id] != null && this.id >= 256) {
				nbt.putShort("id", (short) 0);
			}
		}
	}

	@Inject(method = "readNbt", at = @At("RETURN"))
	private void retroapi$readNbt(NbtCompound nbt, CallbackInfo ci) {
		if (!nbt.contains("retroapi:id")) return;

		String stringId = nbt.getString("retroapi:id");
		String[] parts = stringId.split(":", 2);
		if (parts.length != 2) return;

		RetroIdentifier retroId = new RetroIdentifier(parts[0], parts[1]);

		BlockRegistration blockReg = RetroRegistry.getBlockById(retroId);
		if (blockReg != null) {
			this.id = blockReg.getBlock().id;
			return;
		}

		ItemRegistration itemReg = RetroRegistry.getItemById(retroId);
		if (itemReg != null) {
			this.id = itemReg.getItem().id;
			return;
		}

		// Missing mod
		this.id = 0;
		this.size = 0;
	}

	private static String resolveStringId(int numericId) {
		if (numericId >= 0 && numericId < Block.BY_ID.length) {
			Block block = Block.BY_ID[numericId];
			if (block != null) {
				BlockRegistration reg = RetroRegistry.getBlockRegistration(block);
				if (reg != null) return reg.getId().toString();
			}
		}
		if (numericId >= 0 && numericId < Item.BY_ID.length) {
			Item item = Item.BY_ID[numericId];
			if (item != null) {
				ItemRegistration reg = RetroRegistry.getItemRegistration(item);
				if (reg != null) return reg.getId().toString();
			}
		}
		return null;
	}
}
