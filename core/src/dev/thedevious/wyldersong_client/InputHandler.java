package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import org.json.JSONObject;

public class InputHandler {
	public Client client;
	public Entity player;
	public int mouseX = 0;
	public int mouseY = 0;
	public long mouseCellX = 0;
	public long mouseCellY = 0;
	public long screenCellX = 0;
	public long screenCellY = 0;
	public long screenUICellX = 0;
	public long screenUICellY = 0;

	public InputHandler(Client client, Entity player) {
		this.client = client;
		this.player = player;
	}

	public void update() {
		if (client.isReady) {
			this.mouseX = Gdx.input.getX();
			this.mouseY = Gdx.input.getY();

			// TODO: Don't repeat yourself...
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				client.isReady = false;
				JSONObject object = new JSONObject();
				object.put("type", "MovePlayer");
				object.put("id", player.id);
				object.put("value", "MoveNorth");
				client.send(object);
			}

			if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				client.isReady = false;
				JSONObject object = new JSONObject();
				object.put("type", "MovePlayer");
				object.put("id", player.id);
				object.put("value", "MoveSouth");
				client.send(object);
			}

			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				client.isReady = false;
				JSONObject object = new JSONObject();
				object.put("type", "MovePlayer");
				object.put("id", player.id);
				object.put("value", "MoveWest");
				client.send(object);
			}

			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				client.isReady = false;
				JSONObject object = new JSONObject();
				object.put("type", "MovePlayer");
				object.put("id", player.id);
				object.put("value", "MoveEast");
				client.send(object);
			}
		}
	}
}
