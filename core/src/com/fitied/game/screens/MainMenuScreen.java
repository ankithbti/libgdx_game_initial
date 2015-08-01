package com.fitied.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fitied.game.Application;
import com.sun.glass.events.TouchEvent;
import com.sun.javafx.scene.control.behavior.TextBinding;

public class MainMenuScreen implements Screen {

	private Application _app;
	private ShapeRenderer _shapeRenderer;
	private Stage _stage;
	private Skin _skin;
	private TextButton _buttonPlay;
	private TextButton _buttonExit;

	public MainMenuScreen(final Application app) {
		_app = app;
		_shapeRenderer = new ShapeRenderer();
		_stage = new Stage(new FitViewport(Application.VIEWPORT_WIDTH, Application.VIEWPORT_HEIGHT, _app._camera));

	}

	@Override
	public void show() {
		System.out.println("MainMenuScreen::show()");

		// Set the InputHandler as Stage.
		// Delegating the Input Handling to Stage which internally delegate it
		// to Actors
		Gdx.input.setInputProcessor(_stage);
		
		// To clear the Stage
		_stage.clear();

		_skin = new Skin();
		_skin.addRegions(_app._assetManager.get("ui/uiskin.atlas", TextureAtlas.class));
		_skin.add("default-font", _app._font);
		_skin.load(Gdx.files.internal("ui/uiskin.json"));

		initButtons();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update(delta);

		_stage.draw();

		// Draw font
		_app._batch.begin();
		_app._font.setColor(Color.DARK_GRAY);
		_app._font.draw(_app._batch, "MainMenu", 100, 100);
		_app._batch.end();

	}

	private void update(float delta) {
		_stage.act(delta);
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("MainMenuScreen::resize()");
		_stage.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {
		System.out.println("MainMenuScreen::pause()");

	}

	@Override
	public void resume() {
		System.out.println("MainMenuScreen::resume()");

	}

	@Override
	public void hide() {
		System.out.println("MainMenuScreen::hide()");

	}

	@Override
	public void dispose() {
		_stage.dispose();
		_shapeRenderer.dispose();
		System.out.println("MainMenuScreen::dispose()");

	}

	private void initButtons() {
		_buttonPlay = new TextButton("Play", _skin);
		_buttonPlay.setPosition(110, 260);
		_buttonPlay.setSize(200, 60);
		_buttonPlay.addAction(Actions.sequence(
				Actions.alpha(0f), 
				Actions.parallel(Actions.fadeIn(0.5f), Actions.moveBy(0f, -20f, 0.5f, Interpolation.pow5Out))
				));

		_buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				_app.setScreen(_app._playScreen);
			}
		});

		_buttonExit = new TextButton("Exit", _skin);
		_buttonExit.setPosition(110, 190);
		_buttonExit.setSize(200, 60);
		_buttonExit.addAction(Actions.sequence(
				Actions.alpha(0f), 
				Actions.parallel(Actions.fadeIn(0.5f), Actions.moveBy(0f, -20f, 0.5f, Interpolation.pow5Out))
				));
		
		_buttonExit.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				Gdx.app.exit();
			}

		});

		_stage.addActor(_buttonPlay);
		_stage.addActor(_buttonExit);
	}

}
