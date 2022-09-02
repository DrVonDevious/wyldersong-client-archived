package dev.thedevious.wyldersong_client;

import com.badlogic.gdx.Gdx;
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

	public int screenWidth;
	public int screenHeight;
	private static final float SCALE = 1.5f;
	private static final int CELL_SIZE = 8;
	private Texture tilesetTexture;
	private final SpriteBatch batch;
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

	public Terminal(Game game, TerminalConfig config) {
		this.game = game;
		this.config = config;
		this.screenWidth = (int) (config.width * CELL_SIZE * SCALE);
		this.screenHeight = (int) (config.height * CELL_SIZE * SCALE);
		this.entities = new ArrayList<>();
		this.structures = new ArrayList<>();
		this.tiles = new ArrayList<>();

		Gdx.graphics.setTitle(config.title);
		Gdx.graphics.setWindowedMode(this.screenWidth, this.screenHeight);

		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		this.batch = new SpriteBatch();
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

		ScreenUtils.clear(0, 0, 0, 1);

		if (input != null) {
			Vector3 mousePosition = game.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			input.mouseCellX = Math.round((mousePosition.x / CELL_SIZE / SCALE) - 0.5);
			input.mouseCellY = -Math.round(((mousePosition.y - screenHeight) / CELL_SIZE / SCALE) - 0.5);
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

		if (playerEntity != null && structures != null) {
			print(playerEntity.x - 39, playerEntity.y - 23, "X: " + playerEntity.x + " Y: " + playerEntity.y);

			for (Entity entity : structures) {
				if (entity.x == input.mouseCellX && entity.y == input.mouseCellY) {
					print(playerEntity.x - 40, playerEntity.y + 25, entity.name);
				}
			}

			for (Entity entity : entities) {
				if (entity.x == input.mouseCellX && entity.y == input.mouseCellY) {
					print(playerEntity.x - 40, playerEntity.y + 25, "DrVon");
				}
			}
		}

		batch.end();
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

	public void draw(int x, int y, int glyph, Color bg, Color fg) {
		fill(x, y, bg);
		batch.setColor(fg);
		batch.draw(
			glyphs[glyph],
			x * CELL_SIZE * SCALE,
			(config.height - y) * CELL_SIZE * SCALE,
			CELL_SIZE * SCALE,
			CELL_SIZE * SCALE
		);
	}

	public void print(int x, int y, String string) {
		for (int i = 0; i < string.length(); i++) {
			fill(x + i, y, Color.BLACK);
			draw(x + i, y, Util.toGlyph(string.charAt(i)), Color.BLACK, Color.WHITE);
		}
	}

	public void print(int x, int y, String string, boolean centered) {
		if (centered) {
			print(x - (string.length() / 2), y, string);
		} else {
			print(x, y, string);
		}
	}
}
