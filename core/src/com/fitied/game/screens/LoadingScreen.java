package com.fitied.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fitied.game.Application;

public class LoadingScreen implements Screen {

	private final Application _app;
	private ShapeRenderer _shapeRenderer;
	private float _progress;

	public LoadingScreen(final Application app) {
		_app = app;
		_shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void show() {
		System.out.println("LoadingScreen::show()");
		_shapeRenderer.setProjectionMatrix(_app._camera.combined);
		_progress = 0.0f;
		queueAssets();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update(delta);

		_app._batch.begin();
		_app._font.setColor(Color.BLACK);
		_app._font.draw(_app._batch, "Loading", _app._camera.viewportWidth / 2 - 45, _app._camera.viewportHeight / 2 + 30);
		_app._batch.end();

		// Draw Progress Bar
		_shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		_shapeRenderer.setColor(Color.BLACK);
		_shapeRenderer.rect(32, _app._camera.viewportHeight / 2 - 8, _app._camera.viewportWidth - 64, 16);
		_shapeRenderer.setColor(Color.GREEN);
		_shapeRenderer.rect(32, _app._camera.viewportHeight / 2 - 8, _progress * (_app._camera.viewportWidth - 64), 16);
		_shapeRenderer.end();
	}

	private void update(float delta) {
		_progress = MathUtils.lerp(_progress, _app._assetManager.getProgress(), 0.1f);
		// Second condition is to make sure it goes to next screen only after
		// loading fully the BAR
		if (_app._assetManager.update()) {
			// Once finished loading the control will come here....
			//_app.setScreen(_app._splashScreen);
			_app.setScreen(_app._box2dScreen);
		}

	}

	@Override
	public void resize(int width, int height) {
		System.out.println("LoadingScreen::resize()");

	}

	@Override
	public void pause() {
		System.out.println("LoadingScreen::pause()");

	}

	@Override
	public void resume() {
		System.out.println("LoadingScreen::resume()");

	}

	@Override
	public void hide() {
		System.out.println("LoadingScreen::hide()");

	}

	@Override
	public void dispose() {
		_shapeRenderer.dispose();
		System.out.println("LoadingScreen::dispose()");
	}

	private void queueAssets() {
		_app._assetManager.load("splash.jpg", Texture.class);
		_app._assetManager.load("bigImage.jpg", Texture.class);
		_app._assetManager.load("ui/uiskin.atlas", TextureAtlas.class);
		_app._assetManager.load("music/mario_backgroundMusic.mp3", Music.class);
		_app._assetManager.load("sound/mario_jump.wav", Sound.class);
		_app._assetManager.load("sound/mario_coin.wav", Sound.class);
		_app._assetManager.load("mario1.png", Texture.class);
		_app._assetManager.load("playerSprite.png", Texture.class);
		_app._assetManager.load("coinSprite.png", Texture.class);
		//_app._assetManager.load("ui/uiskin.json", FileHandle.class);
		System.out.println("LoadingScreen::queueAssets() - Assets Loaded....");
	}

}
