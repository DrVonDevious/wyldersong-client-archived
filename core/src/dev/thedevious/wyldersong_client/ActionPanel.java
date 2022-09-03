package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;
import java.util.List;

public class ActionPanel extends UIPanel {
	public List<String> actions;

	public ActionPanel(Terminal terminal) {
		this.width = 14;
		this.height = 20;
		this.actions = new ArrayList<>();
		this.terminal = terminal;
	}

	@Override
	public void render(OrthographicCamera camera) {
		super.render(camera);

		if (this.isOpen) {
			for (String action : actions) {
				boolean isHovering = (terminal.input.mouseCellY == this.normalizedY - terminal.playerEntity.y + 1);
				terminal.print(
					this.normalizedX - terminal.playerEntity.x + 1,
					this.normalizedY - terminal.playerEntity.y + 1,
					action,
					isHovering ? Color.GRAY : Color.BLACK,
					Color.WHITE
				);
			}
		}
	}
}
