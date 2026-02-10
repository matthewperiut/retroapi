package com.periut.retroapi.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
	@Accessor("id")
	int retroapi$getId();

	@Mutable
	@Accessor("id")
	void retroapi$setId(int id);

	@Accessor("BY_ID")
	static Item[] retroapi$getByIdArray() {
		throw new AssertionError();
	}

	@Accessor("key")
	String retroapi$getKey();

	@Accessor("key")
	void retroapi$setKey(String key);
}
