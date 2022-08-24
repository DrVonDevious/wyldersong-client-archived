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
		// TODO: Don't repeat yourself...
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			JSONObject object = new JSONObject();
			object.put("type", "PlayerUpdate");
			object.put("id", player.id);
			object.put("y", player.y - 8);
			object.put("x", player.x);
			client.send(object);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			JSONObject object = new JSONObject();
			object.put("type", "PlayerUpdate");
			object.put("id", player.id);
			object.put("y", player.y + 8);
			object.put("x", player.x);
			client.send(object);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			JSONObject object = new JSONObject();
			object.put("type", "PlayerUpdate");
			object.put("id", player.id);
			object.put("y", player.y);
			object.put("x", player.x - 8);
			client.send(object);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			JSONObject object = new JSONObject();
			object.put("type", "PlayerUpdate");
			object.put("id", player.id);
			object.put("y", player.y);
			object.put("x", player.x + 8);
			client.send(object);
		}
	}
}
