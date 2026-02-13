package com.periut.retroapi.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {
	@Invoker("<init>")
	static Block retroapi$create(int id, Material material) {
		throw new AssertionError();
	}

	@Accessor("id")
	int retroapi$getId();

	@Mutable
	@Accessor("id")
	void retroapi$setId(int id);

	@Accessor("BY_ID")
	static Block[] retroapi$getByIdArray() {
		throw new AssertionError();
	}

	@Accessor("key")
	String retroapi$get2Key();

	@Accessor("key")
	void retroapi$setKey(String key);
}
