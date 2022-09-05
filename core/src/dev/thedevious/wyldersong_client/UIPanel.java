package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;

public class UIPanel {
	public boolean isOpen = false;
	public int width = 5;
	public int height = 5;
	public int originX = 1;
	public int originY = 1;

	public Terminal terminal;

	public void render() {
		if (isOpen) {
			terminal.fillUI(
				originX,
					(int) (originY + height * Terminal.SCALE + 4),
				width,
				height,
				Color.BLACK
			);
			drawBorder(originX, (originY + height + 4), terminal);
		}
	}

	private void drawBorder(int x, int y, Terminal terminal) {
		terminal.drawUI(x, y, 218, Color.BLACK, Color.WHITE);
		terminal.drawUI(x + width - 1, y, 191, Color.BLACK, Color.WHITE);
		terminal.drawUI(x, y + height - 1, 192, Color.BLACK, Color.WHITE);
		terminal.drawUI(x + width - 1, y + height - 1, 217, Color.BLACK, Color.WHITE);

		for (int i = 1; i < width - 1; i++) {
			terminal.drawUI(x + i, y, 196, Color.BLACK, Color.WHITE);
		}

		for (int i = 1; i < width - 1; i++) {
			terminal.drawUI(x + i, y + height - 1, 196, Color.BLACK, Color.WHITE);
		}

		for (int i = 1; i < height - 1; i++) {
			terminal.drawUI(x, y + i, 179, Color.BLACK, Color.WHITE);
		}

		for (int i = 1; i < height - 1; i++) {
			terminal.drawUI(x + width - 1, y + i, 179, Color.BLACK, Color.WHITE);
		}
	}
}
