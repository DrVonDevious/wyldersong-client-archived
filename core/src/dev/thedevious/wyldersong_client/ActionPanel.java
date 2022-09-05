package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class ActionPanel extends UIPanel {
	public List<String> actions;

	public ActionPanel(Terminal terminal) {
		this.width = 16;
		this.height = 20;
		this.actions = new ArrayList<>();
		this.terminal = terminal;
	}

	@Override
	public void render() {
		super.render();

		if (this.isOpen) {
			for (int i = 0; i < actions.size(); i++) {
				boolean isHovering = (
					terminal.input.screenUICellY == this.originY + height + 4 + i &&
					terminal.input.screenUICellX > this.originX &&
					terminal.input.screenUICellX < this.originX + width
				);

				terminal.printUI(
					this.originX + 1,
					(this.originY + height + 5 + i),
					actions.get(i),
					isHovering ? Color.GRAY : Color.BLACK,
					Color.WHITE
				);
			}
		}
	}
}
