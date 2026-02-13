package com.periut.retroapi.mixin;

import com.periut.retroapi.api.*;
import com.periut.retroapi.compat.StationAPICompat;
import com.periut.retroapi.registry.ItemRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class ItemMixin implements RetroItemAccess {

	@Shadow public abstract Item setSprite(int sprite);

	@Override
	public RetroItemAccess retroapi$texture(RetroIdentifier textureId) {
		Item self = (Item) (Object) this;
		RetroTexture tex = RetroTextures.addItemTexture(textureId);
		self.setSprite(tex.id);
		RetroTextures.trackItem(self, tex);
		return this;
	}

	@Override
	public Item retroapi$register(RetroIdentifier id) {
		Item self = (Item) (Object) this;
		RetroRegistry.registerItem(new ItemRegistration(id, self));

		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			StationAPICompat.registerItem(id.namespace(), id.path(), self);
		}

		return self;
	}
}
