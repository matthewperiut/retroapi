package com.periut.retroapi.mixin.register;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventorySlot.class)
public interface InventorySlotAccessor {
	@Accessor("inventory")
	Inventory getInventory();
}
