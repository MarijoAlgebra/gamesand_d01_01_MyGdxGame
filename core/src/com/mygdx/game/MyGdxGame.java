package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {

	private BitmapFont font;
	private int score;

	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	private OrthographicCamera camera;

	private SpriteBatch spriteBatch;

	private Rectangle bucket;
	private Array<Rectangle> raindrops;

	private long lastDropTime;
	
	@Override
	public void create () {

		font = new BitmapFont();
		font.getData().setScale(2, 2);
		score = 0;

		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		spriteBatch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800/2 - 64/2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		spriteBatch.begin();
		spriteBatch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			spriteBatch.draw(dropImage, raindrop.x, raindrop.y);
		}
		font.draw(spriteBatch, "score: " + score, 30, 450);
		spriteBatch.end();

		if (Gdx.input.isTouched()) {
			bucket.x = Gdx.input.getX() - 64/2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		}

		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		Iterator<Rectangle> iterator = raindrops.iterator();
		while (iterator.hasNext()) {
			Rectangle raindrop = iterator.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0) {
				iterator.remove();
			}
			if (raindrop.overlaps(bucket)) {
				dropSound.play();
				score += 1;
				iterator.remove();
			}
		}
	}
	
	@Override
	public void dispose () {
		dropSound.dispose();
		rainMusic.dispose();
		bucketImage.dispose();
		dropImage.dispose();
		spriteBatch.dispose();
		font.dispose();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}
