package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Blending.None;

public class Terminal extends ScreenAdapter {
	public TerminalConfig config;
	public UIConfig uiConfig;

	public int screenWidth;
	public int screenHeight;
	public static final float SCALE = 2.0f;
	public static final float UI_SCALE = 1.0f;
	public static final int CELL_SIZE = 8;
	private Texture tilesetTexture;
	private final SpriteBatch batch;
	private final SpriteBatch uiBatch;
	private final ShapeRenderer shapeRenderer;
	public InputHandler input;
	private Pixmap pixmap;
	private final TextureRegion[] glyphs;
	public Entity playerEntity;
	public List<Entity> entities;
	public List<Entity> structures;
	public List<Entity> tiles;
	public boolean isMapLoaded = false;
	private final Game game;
	private final ActionPanel actionPanel;
	private final InventoryPanel inventoryPanel;

	public Terminal(Game game, TerminalConfig config, UIConfig uiConfig) {
		this.game = game;
		this.config = config;
		this.uiConfig = uiConfig;
		this.screenWidth = (int) (config.width * CELL_SIZE * SCALE);
		this.screenHeight = (int) (config.height * CELL_SIZE * SCALE);
		this.entities = new ArrayList<>();
		this.structures = new ArrayList<>();
		this.tiles = new ArrayList<>();
		this.actionPanel = new ActionPanel(this);
		this.inventoryPanel = new InventoryPanel(this);

		Gdx.graphics.setTitle(config.title);
		Gdx.graphics.setWindowedMode(this.screenWidth, this.screenHeight);

		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		this.batch = new SpriteBatch();
		this.uiBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.tilesetTexture = new Texture("tilesets/" + config.tileset);

		pixmap = new Pixmap(Gdx.files.internal("tilesets/" + config.tileset));
		pixmap = applyTransparencyMask(pixmap);
		this.tilesetTexture = new Texture(pixmap);

		glyphs = new TextureRegion[256];
		for(int i = 0; i < 256; i++) {
			int x = (i % 16) * CELL_SIZE;
			int y = (i / 16) * CELL_SIZE;

			glyphs[i] = new TextureRegion(tilesetTexture, x, y, CELL_SIZE, CELL_SIZE);
		}
	}

	public Pixmap applyTransparencyMask(Pixmap pixmap) {
		Pixmap result = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
		result.setBlending(None);
		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getWidth(); y++) {
				// Make pink color transparent
				if (pixmap.getPixel(x, y) != -16711681) {
					result.drawPixel(x, y, pixmap.getPixel(x, y));
				}
			}
		}

		return result;
	}

	@Override
	public void render(float delta) {
		if (input != null) {
			input.update();
		}

		ScreenUtils.clear(0, 0, 0.1f, 1);

		if (input != null) {
			Vector3 mousePosition = game.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			input.mouseCellX = Math.round((mousePosition.x / CELL_SIZE / SCALE) - 0.5);
			input.mouseCellY = -Math.round(((mousePosition.y - screenHeight) / CELL_SIZE / SCALE) - 0.5);
			input.screenCellX = Math.round((Gdx.input.getX() / SCALE / CELL_SIZE) - 0.5);
			input.screenCellY = Math.round(((Gdx.input.getY()) / SCALE / CELL_SIZE) - 0.5);
			input.screenUICellX = Math.round((Gdx.input.getX() / UI_SCALE / CELL_SIZE) - 0.5);
			input.screenUICellY = Math.round(((Gdx.input.getY()) / UI_SCALE / CELL_SIZE) - 0.5);
		}

		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			actionPanel.isOpen = false;
		}

		if (playerEntity != null && Gdx.input.isKeyJustPressed(Input.Keys.I)) {
			System.out.println((int) (screenWidth / CELL_SIZE / SCALE) - inventoryPanel.width);
			inventoryPanel.originX = (int) ((screenWidth / CELL_SIZE / UI_SCALE) - inventoryPanel.width);
			inventoryPanel.originY = -inventoryPanel.height - 3;
			inventoryPanel.isOpen = !inventoryPanel.isOpen;
		}

		if (playerEntity != null) {
			game.camera.position.set(
				playerEntity.x * CELL_SIZE * SCALE,
				(screenHeight) - (playerEntity.y * CELL_SIZE * SCALE),
				0
			);
		}

		game.camera.update();

		batch.setProjectionMatrix(game.camera.combined);

		batch.begin();

		if (!game.isConnected) {
			print(config.width / 2, config.height / 2, "Connecting...", true);
		}

		if (tiles != null && isMapLoaded) {
			for (Entity entity : tiles) {
				draw(entity.x, entity.y, entity.glyph, entity.bg, entity.fg);
			}
		}

		if (structures != null && isMapLoaded) {
			for (Entity entity : structures) {
				draw(entity.x, entity.y, entity.glyph, entity.bg, entity.fg);
			}
		}

		if (playerEntity != null) {
			draw(playerEntity.x, playerEntity.y, playerEntity.glyph, playerEntity.bg, playerEntity.fg);
		}

		if (entities != null) {
			for (Entity entity : entities) {
				draw(entity.x, entity.y, entity.glyph, entity.bg, entity.fg);
			}
		}

		batch.end();

		uiBatch.begin();

		if (playerEntity != null && structures != null) {
			if (game.isDebug) {
				printUI(0, 1, "X: " + playerEntity.x + " Y: " + playerEntity.y);
				printUI(0, 2, input.screenCellX + ", " + input.screenCellY);
				printUI(0, 3, "FPS: " + Gdx.graphics.getFramesPerSecond());
			}

			for (Entity entity : structures) {
				if (entity.x == input.mouseCellX && entity.y == input.mouseCellY) {
					printUI(0, (int) (config.height * SCALE), entity.name);

					if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
						actionPanel.actions.clear();
						actionPanel.originX = (int) ((input.screenCellX + 1) * SCALE);
						actionPanel.originY = (int) ((input.screenCellY + 2) * SCALE - 25);
						actionPanel.isOpen = true;
						actionPanel.actions.add("Fell Tree");
						actionPanel.actions.add("Take Branches");
						actionPanel.actions.add("Take Bark");
						actionPanel.actions.add("Forage");
					}
				}
			}

			for (Entity entity : entities) {
				if (entity.x == input.mouseCellX && entity.y == input.mouseCellY) {
					printUI(0, 0, "DrVon");
				}
			}
		}

		actionPanel.render();
		inventoryPanel.render();

		uiBatch.end();
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
		tilesetTexture.dispose();
	}

	public void fill(int x, int y, Color color) {
		batch.setColor(color);
		batch.draw(
			glyphs[219],
			x * CELL_SIZE * SCALE,
			(config.height - y) * CELL_SIZE * SCALE,
			CELL_SIZE * SCALE,
			CELL_SIZE * SCALE
		);
	}

	public void fill(int x, int y, int width, int height, Color color) {
		batch.setColor(color);

		for (int cell_y = 0; cell_y < height; cell_y++) {
			for (int cell_x = 0; cell_x < width; cell_x++) {
				batch.draw(
					glyphs[219],
					(x + cell_x) * CELL_SIZE * SCALE,
					((config.height - y) + cell_y + 1) * CELL_SIZE * SCALE,
					CELL_SIZE * SCALE,
					CELL_SIZE * SCALE
				);
			}
		}
	}

	public void fillUI(int x, int y, Color color) {
		uiBatch.setColor(color);
		uiBatch.draw(
			glyphs[219],
			x * CELL_SIZE * UI_SCALE,
			(config.height * SCALE - y) * CELL_SIZE * UI_SCALE,
			CELL_SIZE * UI_SCALE,
			CELL_SIZE * UI_SCALE
		);
	}

	public void fillUI(int x, int y, int width, int height, Color color) {
		uiBatch.setColor(color);

		for (int cell_y = 0; cell_y < height; cell_y++) {
			for (int cell_x = 0; cell_x < width; cell_x++) {
				uiBatch.draw(
					glyphs[219],
					(x + cell_x) * CELL_SIZE * UI_SCALE,
					((config.height * SCALE - y) + cell_y + 1) * CELL_SIZE * UI_SCALE,
					CELL_SIZE * UI_SCALE,
					CELL_SIZE * UI_SCALE
				);
			}
		}
	}

	public void draw(int x, int y, int glyph, Color fg) {
		batch.setColor(fg);
		batch.draw(
			glyphs[glyph],
			x * CELL_SIZE * SCALE,
			(config.height - y) * CELL_SIZE * SCALE,
			CELL_SIZE * SCALE,
			CELL_SIZE * SCALE
		);
	}

	public void draw(int x, int y, int glyph, Color bg, Color fg) {
		fill(x, y, bg);
		draw(x, y, glyph, fg);
	}

	public void drawUI(int x, int y, int glyph, Color bg, Color fg) {
		fillUI(x, y, bg);
		uiBatch.setColor(fg);
		uiBatch.draw(
			glyphs[glyph],
			x * CELL_SIZE * UI_SCALE,
			(config.height * SCALE - y) * CELL_SIZE * UI_SCALE,
			CELL_SIZE * UI_SCALE,
			CELL_SIZE * UI_SCALE
		);
	}

	public void print(int x, int y, String string) {
		print(x, y, string, Color.BLACK, Color.WHITE);
	}

	public void print(int x, int y, String string, Color bg, Color fg) {
		for (int i = 0; i < string.length(); i++) {
			draw(x + i, y, Util.toGlyph(string.charAt(i)), bg, fg);
		}
	}

	public void print(int x, int y, String string, boolean centered) {
		if (centered) {
			print(x - (string.length() / 2), y, string);
		} else {
			print(x, y, string);
		}
	}

	public void printUI(int x, int y, String string) {
		printUI(x, y, string, Color.BLACK, Color.WHITE);
	}

	public void printUI(int x, int y, String string, Color bg, Color fg) {
		for (int i = 0; i < string.length(); i++) {
			drawUI(x + i, y, Util.toGlyph(string.charAt(i)), bg, fg);
		}
	}
}
