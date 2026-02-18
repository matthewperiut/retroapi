package com.periut.retroapi.testmod;

import com.periut.retroapi.register.RetroIdentifier;
import com.periut.retroapi.register.blockentity.MenuHelper;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

public class TestModClient implements ClientModInitializer {
	@Override
	public void initClient() {
		MenuHelper.registerScreen(
			TestMod.FREEZER_SCREEN,
			FreezerBlockEntity::new,
			(playerInv, inv) -> new FreezerScreen(new FreezerMenu(playerInv, (FreezerBlockEntity) inv))
		);
	}
}
