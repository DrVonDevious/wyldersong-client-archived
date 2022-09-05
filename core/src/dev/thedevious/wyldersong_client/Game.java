package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Game extends com.badlogic.gdx.Game {
	public OrthographicCamera camera;
	private Client client;
	private Terminal terminal;
	private static final String VERSION = "v0.02a";
	public boolean isConnected = false;
	public boolean isDebug = true;

	@Override
	public void create () {
		TerminalConfig config = TerminalConfig.setDefault();
		UIConfig uiConfig = UIConfig.setDefault();
		config.title = "Wyldersong " + VERSION;

		terminal = new Terminal(this, config, uiConfig);
		setScreen(terminal);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, terminal.screenWidth, terminal.screenHeight);
		camera.position.set((float) terminal.screenWidth / 2, (float) terminal.screenHeight / 2, 0);
		camera.update();

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
			Entity player = new Entity(
				UUID.fromString(object.getString("id")),
				object.getInt("x"),
				object.getInt("y"),
				64,
				Color.BLACK,
				Color.WHITE
			);

			player.inventory.add("Flint Axe");
			player.inventory.add("Rabbit Hide Gloves");
			player.inventory.add("Oak Mug");

			terminal.playerEntity = player;
			terminal.input = new InputHandler(client, player);
		} else if (Objects.equals(object.getString("type"), "Player")) {
			Entity entity = new Entity(
				UUID.fromString(object.getString("id")),
				object.getInt("x"),
				object.getInt("y"),
				64,
				Color.BLACK,
				Color.GREEN
			);
			terminal.entities.add(entity);
		} else if (Objects.equals(object.getString("type"), "PlayerUpdate")) {
			if (Objects.equals(object.getString("id"), terminal.playerEntity.id.toString())) {
				terminal.playerEntity.x = object.getInt("x");
				terminal.playerEntity.y = object.getInt("y");
				client.isReady = true;
			} else {
				for (Entity entity : terminal.entities) {
					if (Objects.equals(object.getString("id"), entity.id.toString())) {
						entity.y = object.getInt("y");
						entity.x = object.getInt("x");
					}
				}
			}
		}

		if (Objects.equals(object.getString("type"), "Structure")) {
			if (Objects.equals(object.getString("sub_type"), "Fence")) {
				Entity entity = new Entity(
					UUID.randomUUID(),
					object.getInt("x"),
					object.getInt("y"),
					20,
					Color.BLACK,
					Color.BROWN
				);

				entity.name = object.getString("name");

				terminal.structures.add(entity);
			}

			if (Objects.equals(object.getString("sub_type"), "Tree")) {
				Entity entity = new Entity(
					UUID.randomUUID(),
					object.getInt("x"),
					object.getInt("y"),
					79,
					Color.BLACK,
					Color.BROWN
				);

				entity.name = object.getString("name");

				terminal.structures.add(entity);
			}
		}

		if (Objects.equals(object.getString("type"), "Tile")) {
			if (Objects.equals(object.getString("tile_type"), "grass_tile")) {
				int grassColorChance = ThreadLocalRandom.current().nextInt(0, 99 + 1);
				int grassGlyphChance = ThreadLocalRandom.current().nextInt(0, 99 + 1);
				Color grassColor;
				int grassGlyph;

				if (grassColorChance < 20) {
					grassColor = new Color(0.7f, 0.6f, 0.1f, 1);
				} else {
					grassColor = new Color(0.4f, 0.8f, 0.1f, 1);
				}

				if (grassGlyphChance < 10) {
					grassGlyph = 96;
				} else {
					grassGlyph = 44;
				}

				Entity entity = new Entity(
					UUID.randomUUID(),
					object.getInt("x"),
					object.getInt("y"),
					grassGlyph,
					new Color(0, 0, 0.1f, 1),
					grassColor
				);
				terminal.tiles.add(entity);
			}
		}

		if (Objects.equals(object.getString("type"), "Started_Loading")) {
			terminal.isMapLoaded = false;
		}

		if (Objects.equals(object.getString("type"), "Finished_Loading")) {
			terminal.isMapLoaded = true;
		}
	}
}
