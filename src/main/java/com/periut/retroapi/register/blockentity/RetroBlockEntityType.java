package com.periut.retroapi.register.blockentity;

import com.periut.retroapi.register.RetroIdentifier;
import com.periut.retroapi.mixin.register.BlockEntityAccessor;
import net.minecraft.block.entity.BlockEntity;

import java.util.function.Supplier;

public class RetroBlockEntityType<T extends BlockEntity> {
	private final RetroIdentifier id;
	private final Class<T> clazz;
	private final Supplier<T> factory;

	public RetroBlockEntityType(RetroIdentifier id, Class<T> clazz, Supplier<T> factory) {
		this.id = id;
		this.clazz = clazz;
		this.factory = factory;
		BlockEntityAccessor.invokeRegister(clazz, id.toString());
	}

	public T create() {
		return factory.get();
	}

	public RetroIdentifier getId() {
		return id;
	}
}
