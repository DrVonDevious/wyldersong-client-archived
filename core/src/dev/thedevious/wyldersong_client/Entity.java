package dev.thedevious.wyldersong_client;

import java.util.UUID;

public class Entity {
	public UUID id;
	public int x;
	public int y;
	public int glyph;

	public Entity(UUID id, int x, int y, int glyph) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.glyph = glyph;
	}
}
