package net.minecraft.locale;

import java.io.IOException;
import java.util.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Language {
	private static Language INSTANCE = new Language();
	private Properties translations = new Properties();

	private Language() {
		try {
			this.translations.load(Language.class.getResourceAsStream("/lang/en_US.lang"));
			this.translations.load(Language.class.getResourceAsStream("/lang/stats_US.lang"));
		} catch (IOException var2) {
			var2.printStackTrace();
		}
	}

	public static Language getInstance() {
		return INSTANCE;
	}

	public String translate(String key) {
		return this.translations.getProperty(key, key);
	}

	public String translate(String key, Object... args) {
		String string = this.translations.getProperty(key, key);
		return String.format(string, args);
	}

	@Environment(EnvType.CLIENT)
	public String translateName(String key) {
		return this.translations.getProperty(key + ".name", "");
	}
}
