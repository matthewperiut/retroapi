package com.periut.retroapi.texture;

import com.periut.retroapi.registry.BlockRegistration;
import com.periut.retroapi.registry.ItemRegistration;
import com.periut.retroapi.registry.RetroRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AtlasExpander {
	private static final Logger LOGGER = LogManager.getLogger("RetroAPI/AtlasExpander");
	private static final int SPRITE_SIZE = 16;
	private static final int ATLAS_COLUMNS = 16;

	public static BufferedImage expandTerrainAtlas(BufferedImage original) {
		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			return original;
		}

		List<BlockRegistration> blocks = RetroRegistry.getBlocks();
		if (blocks.isEmpty()) {
			return original;
		}

		int spriteSize = original.getWidth() / ATLAS_COLUMNS;
		int originalRows = original.getHeight() / spriteSize;

		// Find first free slot
		int nextFreeSlot = findFirstFreeTerrainSlot();

		// Calculate if we need to expand
		int neededSlots = nextFreeSlot + blocks.size();
		int neededRows = Math.max(originalRows, (neededSlots + ATLAS_COLUMNS - 1) / ATLAS_COLUMNS);

		// Always create a new image (copy) so we don't modify the original
		BufferedImage atlas = new BufferedImage(original.getWidth(), neededRows * spriteSize, BufferedImage.TYPE_INT_ARGB);
		atlas.getGraphics().drawImage(original, 0, 0, null);

		int slot = nextFreeSlot;
		for (BlockRegistration reg : blocks) {
			BufferedImage texture = loadTexture(
				reg.getTextureNamespace(),
				"block/" + reg.getTexturePath()
			);
			if (texture != null) {
				int col = slot % ATLAS_COLUMNS;
				int row = slot / ATLAS_COLUMNS;
				atlas.getGraphics().drawImage(texture, col * spriteSize, row * spriteSize, spriteSize, spriteSize, null);
				reg.getBlock().sprite = slot;
				LOGGER.debug("Assigned block texture {} -> sprite slot {}", reg.getId(), slot);
			}
			slot++;
		}

		return atlas;
	}

	public static BufferedImage expandItemAtlas(BufferedImage original) {
		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			return original;
		}

		List<ItemRegistration> items = RetroRegistry.getItems();
		if (items.isEmpty()) {
			return original;
		}

		int spriteSize = original.getWidth() / ATLAS_COLUMNS;
		int originalRows = original.getHeight() / spriteSize;

		int nextFreeSlot = findFirstFreeItemSlot();
		int neededSlots = nextFreeSlot + items.size();
		int neededRows = Math.max(originalRows, (neededSlots + ATLAS_COLUMNS - 1) / ATLAS_COLUMNS);

		// Always create a new image (copy) so we don't modify the original
		BufferedImage atlas = new BufferedImage(original.getWidth(), neededRows * spriteSize, BufferedImage.TYPE_INT_ARGB);
		atlas.getGraphics().drawImage(original, 0, 0, null);

		int slot = nextFreeSlot;
		for (ItemRegistration reg : items) {
			BufferedImage texture = loadTexture(
				reg.getTextureNamespace(),
				"item/" + reg.getTexturePath()
			);
			if (texture != null) {
				int col = slot % ATLAS_COLUMNS;
				int row = slot / ATLAS_COLUMNS;
				atlas.getGraphics().drawImage(texture, col * spriteSize, row * spriteSize, spriteSize, spriteSize, null);
				reg.getItem().setSprite(slot);
				LOGGER.debug("Assigned item texture {} -> sprite slot {}", reg.getId(), slot);
			}
			slot++;
		}

		return atlas;
	}

	private static int findFirstFreeTerrainSlot() {
		// Vanilla b1.7.3 uses terrain.png slots up to about index 112
		// Start scanning from a safe offset
		return 128;
	}

	private static int findFirstFreeItemSlot() {
		// Vanilla b1.7.3 uses gui/items.png slots up to about index 127
		return 128;
	}

	private static BufferedImage loadTexture(String namespace, String texturePath) {
		String resourcePath = "/assets/" + namespace + "/retroapi/textures/" + texturePath + ".png";
		try (InputStream is = AtlasExpander.class.getResourceAsStream(resourcePath)) {
			if (is != null) {
				return ImageIO.read(is);
			}
			LOGGER.warn("Texture not found: {}", resourcePath);
		} catch (IOException e) {
			LOGGER.error("Failed to load texture: {}", resourcePath, e);
		}
		return null;
	}
}
