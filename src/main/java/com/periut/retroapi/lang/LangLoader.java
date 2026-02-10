package com.periut.retroapi.lang;

import com.periut.retroapi.mixin.LanguageAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.locale.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LangLoader {
	private static final Logger LOGGER = LogManager.getLogger("RetroAPI/LangLoader");

	public static void loadTranslations() {
		if (FabricLoader.getInstance().isModLoaded("stationapi")) {
			return;
		}

		Language language = Language.getInstance();
		Properties translations = ((LanguageAccessor) language).retroapi$getTranslations();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			String modId = mod.getMetadata().getId();
			String langPath = "/assets/" + modId + "/retroapi/lang/en_US.lang";

			try (InputStream is = LangLoader.class.getResourceAsStream(langPath)) {
				if (is != null) {
					loadLangFile(is, translations, modId);
				}
			} catch (IOException e) {
				LOGGER.error("Failed to load lang file for mod {}", modId, e);
			}
		}
	}

	private static void loadLangFile(InputStream is, Properties translations, String modId) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			int eq = line.indexOf('=');
			if (eq > 0) {
				String key = line.substring(0, eq);
				String value = line.substring(eq + 1);
				translations.setProperty(key, value);
				count++;
			}
		}
		LOGGER.info("Loaded {} translations from mod {}", count, modId);
	}
}
