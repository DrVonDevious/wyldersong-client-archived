package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.json.JSONObject;

public class InputHandler {
	public Client client;
	public Entity player;

	public InputHandler(Client client, Entity player) {
		this.client = client;
		this.player = player;
	}

	public void update() {
		if (client.isReady) {
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
