package com.periut.retroapi.testmod;

import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import org.lwjgl.opengl.GL11;

public class FreezerScreen extends InventoryMenuScreen {
	private final FreezerMenu freezerMenu;

	public FreezerScreen(FreezerMenu menu) {
		super(menu);
		this.freezerMenu = menu;
	}

	@Override
	protected void renderLabels() {
		textRenderer.draw("Freezer", 60, 6, 4210752);
		textRenderer.draw("Inventory", 8, backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderMenuBackground(float tickDelta) {
		int texId = minecraft.textureManager.load("/gui/furnace.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(texId);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

		// Flame icon (burn progress)
		if (freezerMenu.fuelTime > 0 && freezerMenu.totalFuelTime > 0) {
			int progress = freezerMenu.fuelTime * 12 / freezerMenu.totalFuelTime;
			drawTexture(x + 56, y + 36 + 12 - progress, 176, 12 - progress, 14, progress + 2);
		}

		// Arrow (cook progress)
		if (freezerMenu.cookTime > 0) {
			int progress = freezerMenu.cookTime * 24 / 200;
			drawTexture(x + 79, y + 34, 176, 14, progress + 1, 16);
		}
	}
}
