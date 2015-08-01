package com.fitied.game.screens;

import com.badlogic.gdx.Screen;
import com.fitied.game.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class SplashScreen implements Screen {

	private final Application _app;
	private Sprite _sprite;
	private Texture _texture;
	private Image _image;
	private Stage _stage;
	private boolean _isTouched;

	public SplashScreen(final Application app) {
		System.out.println("Creating SplashScreen");
		_app = app;
		_stage = new Stage(new FitViewport(Application.VIEWPORT_WIDTH, Application.VIEWPORT_HEIGHT, _app._camera)); // Takes
																													// ViewPort
		// _stage = new Stage(new StretchViewport(Application.VIEWPORT_WIDTH,
		// Application.VIEWPORT_HEIGHT, _app._camera)); // Takes a ViewPort
		// _stage = new Stage(new FillViewport(Application.VIEWPORT_WIDTH,
		// Application.VIEWPORT_HEIGHT, _app._camera)); // Takes a ViewPort

	}

	@Override
	public void show() {
		// This gets called when game loads this screen
		System.out.println("SplashScreen::show()");

		// Set the InputHandler as Stage.
		// Delegating the Input Handling to Stage which internally delegate it
		// to Actors
		Gdx.input.setInputProcessor(_stage);

		Runnable transitionRunnable = new Runnable() {

			@Override
			public void run() {
				if (Gdx.input.justTouched()) {
					_app.setScreen(_app._mainMenuScreen);
				}else 
				if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
					_app.setScreen(_app._mainMenuScreen);
				}else{
					_app.setScreen(_app._mainMenuScreen);
				}
			}
		};

		_texture = _app._assetManager.get("splash.jpg", Texture.class);
		_image = new Image(_texture);
		_image.setOrigin(_image.getWidth() / 2, _image.getHeight() / 2);

		// Setting the image to center of stage
		_image.setPosition(_stage.getWidth() / 2 - _texture.getWidth() / 2,
				_stage.getHeight() / 2 + _image.getHeight() / 2);

		// _image.addAction(Actions.sequence(Actions.alpha(0f),
		// /*Actions.rotateBy(-360f, 1f), */ Actions.scaleBy(0.1f, 0.1f, 0.5f),
		// Actions.scaleBy(-0.1f, -0.1f, 0.5f),
		// Actions.parallel(Actions.sequence(Actions.moveBy(10, 0, 0.5f),
		// Actions.moveBy(-20, 0, 1f), Actions.moveBy(10, 0, 0.5f)),
		// Actions.fadeIn(2.0f)) ) );

		_image.addAction(Actions.sequence(Actions.alpha(0f), Actions.scaleTo(0.1f, 0.1f),
				Actions.parallel(Actions.fadeIn(3.0f, Interpolation.pow2),
						Actions.scaleTo(1f, 1f, 2.5f, Interpolation.pow5),
						Actions.moveTo(_stage.getWidth() / 2 - _texture.getWidth() / 2,
								_stage.getHeight() / 2 - _texture.getHeight() / 2, 2f, Interpolation.swing)),
				/* Actions.rotateBy(360f, 2f, Interpolation.pow2), */
				Actions.delay(1.5f), Actions.run(transitionRunnable)));

		_stage.addActor(_image);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update
		update(delta);

		// Draw
		_stage.draw();

		_app._batch.begin();
		_app._font.setColor(Color.ORANGE);
		_app._font.draw(_app._batch, "Touch To Proceed", _app._camera.viewportWidth / 2 - 40, 40);
		_app._batch.end();

	}

	private void update(float delta) {
		_stage.act(delta);
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("SplashScreen::resize()");
		_stage.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {
		System.out.println("SplashScreen::pause()");

	}

	@Override
	public void resume() {
		System.out.println("SplashScreen::resume()");
	}

	@Override
	public void hide() {
		// This gets called when this screen goes away
		System.out.println("SplashScreen::hide()");
	}

	@Override
	public void dispose() {
		System.out.println("Disposing SplashScreen");
		_stage.dispose();
		_texture.dispose();
	}

}
