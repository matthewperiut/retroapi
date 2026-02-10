package com.periut.retroapi.mixin;

import net.minecraft.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {
	@Accessor("block")
	int retroapi$getBlock();

	@Mutable
	@Accessor("block")
	void retroapi$setBlock(int block);
}
