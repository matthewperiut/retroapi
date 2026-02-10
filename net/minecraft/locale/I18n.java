package net.minecraft.locale;

public class I18n {
	private static Language LANGUAGE = Language.getInstance();

	public static String translate(String key) {
		return LANGUAGE.translate(key);
	}

	public static String translate(String key, Object... args) {
		return LANGUAGE.translate(key, args);
	}
}
