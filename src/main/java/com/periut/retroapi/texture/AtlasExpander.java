package com.periut.retroapi.texture;

import com.periut.retroapi.api.RetroTexture;
import com.periut.retroapi.api.RetroTextures;
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
	private static final int ATLAS_COLUMNS = 16;

	public static BufferedImage expandTerrainAtlas(BufferedImage original) {
		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			return original;
		}

		List<RetroTexture> textures = RetroTextures.getTerrainTextures();
		if (textures.isEmpty()) {
			return original;
		}

		int spriteSize = original.getWidth() / ATLAS_COLUMNS;
		int originalRows = original.getHeight() / spriteSize;

		int maxSlot = textures.stream().mapToInt(t -> t.id).max().orElse(0);
		int neededRows = Math.max(originalRows, (maxSlot + ATLAS_COLUMNS) / ATLAS_COLUMNS);

		BufferedImage atlas = new BufferedImage(original.getWidth(), neededRows * spriteSize, BufferedImage.TYPE_INT_ARGB);
		atlas.getGraphics().drawImage(original, 0, 0, null);

		for (RetroTexture tex : textures) {
			BufferedImage texture = loadTexture(tex.getIdentifier().namespace(), "block/" + tex.getIdentifier().path());
			if (texture != null) {
				int col = tex.id % ATLAS_COLUMNS;
				int row = tex.id / ATLAS_COLUMNS;
				atlas.getGraphics().drawImage(texture, col * spriteSize, row * spriteSize, spriteSize, spriteSize, null);
				LOGGER.debug("Composited block texture {} at slot {}", tex.getIdentifier(), tex.id);
			}
		}

		return atlas;
	}

	public static BufferedImage expandItemAtlas(BufferedImage original) {
		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			return original;
		}

		List<RetroTexture> textures = RetroTextures.getItemTextures();
		if (textures.isEmpty()) {
			return original;
		}

		int spriteSize = original.getWidth() / ATLAS_COLUMNS;
		int originalRows = original.getHeight() / spriteSize;

		int maxSlot = textures.stream().mapToInt(t -> t.id).max().orElse(0);
		int neededRows = Math.max(originalRows, (maxSlot + ATLAS_COLUMNS) / ATLAS_COLUMNS);

		BufferedImage atlas = new BufferedImage(original.getWidth(), neededRows * spriteSize, BufferedImage.TYPE_INT_ARGB);
		atlas.getGraphics().drawImage(original, 0, 0, null);

		for (RetroTexture tex : textures) {
			BufferedImage texture = loadTexture(tex.getIdentifier().namespace(), "item/" + tex.getIdentifier().path());
			if (texture != null) {
				int col = tex.id % ATLAS_COLUMNS;
				int row = tex.id / ATLAS_COLUMNS;
				atlas.getGraphics().drawImage(texture, col * spriteSize, row * spriteSize, spriteSize, spriteSize, null);
				LOGGER.debug("Composited item texture {} at slot {}", tex.getIdentifier(), tex.id);
			}
		}

		return atlas;
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
