package dev.thedevious.wyldersong_client;

public class UIConfig {
	public int width;
	public int height;

	public static UIConfig setDefault() {
		UIConfig config = new UIConfig();
		config.width = 100;
		config.height = 80;
		return config;
	}
}
