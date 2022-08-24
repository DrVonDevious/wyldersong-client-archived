package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Game extends com.badlogic.gdx.Game {
	private int screenWidth;
	private int screenHeight;
	public OrthographicCamera camera;
	private Client client;
	private Terminal terminal;
	public boolean isConnected = false;

	@Override
	public void create () {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);

		TerminalConfig config = TerminalConfig.setDefault();
		config.title = "Wyldersong v0.01a";

		terminal = new Terminal(this, config);
		setScreen(terminal);

		client = new Client(this, "localhost", 8080);
		client.connect();
	}

	@Override
	public void dispose() {
		client.disconnect();
		terminal.dispose();
		System.exit(0);
	}

	public void onConnect() {
		System.out.println("Connected!");

		// Connecting can sometimes happen instantly,
		// so we provide a bit of an old-school feel by waiting a second
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.isConnected = true;
	}

	public void onDisconnect() {
		System.out.println("Disconnected...");
		this.isConnected = false;
	}

	public void onMessage(String message) {
		JSONObject object = new JSONObject(message);

		if (Objects.equals(object.getString("type"), "Self")) {
			Entity player = new Entity(UUID.fromString(object.getString("id")), object.getInt("x"), object.getInt("y"), 64);
			terminal.entities.add(player);
			terminal.input = new InputHandler(client, player);
		} else if (Objects.equals(object.getString("type"), "Player")) {
			Entity entity = new Entity(UUID.fromString(object.getString("id")), object.getInt("x"), object.getInt("y"), 2);
			terminal.entities.add(entity);
		} else if (Objects.equals(object.getString("type"), "PlayerUpdate")) {
			for (Entity entity : terminal.entities) {
				if (Objects.equals(object.getString("id"), entity.id.toString())) {
					entity.y = object.getInt("y");
					entity.x = object.getInt("x");
				}
			}
		}
	}
}