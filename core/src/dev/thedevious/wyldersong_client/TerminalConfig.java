package dev.thedevious.wyldersong_client;

public class TerminalConfig {
	public String title;
	public String tileset;
	public int width;
	public int height;

	public static TerminalConfig setDefault() {
		TerminalConfig config = new TerminalConfig();
		config.title = "Terminal Game";
		config.tileset = "ascii.png";
		config.width = 80;
		config.height = 50;
		return config;
	}
}
