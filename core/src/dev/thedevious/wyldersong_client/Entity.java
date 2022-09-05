package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Entity {
	public UUID id;
	public int x;
	public int y;
	public int glyph;
	public Color bg;
	public Color fg;
	public String name;
	public List<String> inventory;

	public Entity(UUID id, int x, int y, int glyph, Color bg, Color fg) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.bg = bg;
		this.fg = fg;
		this.glyph = glyph;
		this.inventory = new ArrayList<>();
	}
}
