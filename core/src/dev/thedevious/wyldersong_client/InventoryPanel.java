package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;

public class InventoryPanel extends UIPanel {
	public InventoryPanel(Terminal terminal) {
		this.width = 40;
		this.height = 50;
		this.terminal = terminal;
	}

	@Override
	public void render() {
		super.render();

		if (this.isOpen) {
			for (int i = 0; i < terminal.playerEntity.inventory.size(); i++) {
				boolean isHovering = (
						terminal.input.screenUICellY == this.originY + height + 4 + i &&
								terminal.input.screenUICellX > this.originX &&
								terminal.input.screenUICellX < this.originX + width
				);

				terminal.printUI(
						this.originX + 1,
						(this.originY + height + 5 + i),
						terminal.playerEntity.inventory.get(i),
						isHovering ? Color.GRAY : Color.BLACK,
						Color.WHITE
				);
			}
		}
	}
}
