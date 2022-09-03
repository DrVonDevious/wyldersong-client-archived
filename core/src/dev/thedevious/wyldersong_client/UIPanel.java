package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class UIPanel {
	public boolean isOpen = false;
	public int width = 5;
	public int height = 5;
	public int originX = 1;
	public int originY = 1;
	public int normalizedX = 0;
	public int normalizedY = 0;

	public Terminal terminal;

	public void render(OrthographicCamera camera) {
		if (isOpen) {
			normalizedX = (int) (camera.position.x / 8 / 1.5f) + 1 + originX;
			normalizedY = (int) (-camera.position.y / 8 / 1.5f) + 1 + originY + 50;
			terminal.fill(
				normalizedX - terminal.playerEntity.x,
				(normalizedY - terminal.playerEntity.y) + height,
				width,
				height,
				Color.BLACK
			);
			drawBorder(normalizedX - terminal.playerEntity.x, (normalizedY - terminal.playerEntity.y) + height, terminal);
		}
	}

	private void drawBorder(int x, int y, Terminal terminal) {
		terminal.draw(x, y - height, 218, Color.BLACK, Color.WHITE);
		terminal.draw(x + width - 1, y - height, 191, Color.BLACK, Color.WHITE);
		terminal.draw(x, y - 1, 192, Color.BLACK, Color.WHITE);
		terminal.draw(x + width - 1, y - 1, 217, Color.BLACK, Color.WHITE);

		for (int i = 1; i < width - 1; i++) {
			terminal.draw(x + i, y - height, 196, Color.BLACK, Color.WHITE);
		}

		for (int i = 1; i < width - 1; i++) {
			terminal.draw(x + i, y - 1, 196, Color.BLACK, Color.WHITE);
		}

		for (int i = 1; i < height - 1; i++) {
			terminal.draw(x, y - height + i, 179, Color.BLACK, Color.WHITE);
		}

		for (int i = 1; i < height - 1; i++) {
			terminal.draw(x + width - 1, y - height + i, 179, Color.BLACK, Color.WHITE);
		}
	}
}
