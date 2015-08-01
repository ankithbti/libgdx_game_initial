package com.fitied.game.screens;

import org.omg.PortableServer._ServantLocatorStub;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.steer.behaviors.Alignment;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fitied.game.Application;
import com.fitied.game.actors.SlideButton;

public class PlayScreen implements Screen {

	private final Application _app;
	private Stage _stage;
	private Skin _skin;

	// Game Grid
	private int _boardSize = 4;
	private int _holdX, _holdY;

	// Nav-buttons
	private TextButton _backButton;
	private SlideButton[][] _slideButtonGrid;
	
	private Label _infoLabel;

	public PlayScreen(final Application app) {
		_app = app;
		_stage = new Stage(new FitViewport(Application.VIEWPORT_WIDTH, Application.VIEWPORT_HEIGHT, _app._camera));
		
	}

	@Override
	public void show() {
		System.out.println("PlayScreen::show()");

		// Set the InputHandler as Stage.
		// Delegating the Input Handling to Stage which internally delegate it
		// to Actors
		Gdx.input.setInputProcessor(_stage);

		_stage.clear();

		_skin = new Skin();
		_skin.addRegions(_app._assetManager.get("ui/uiskin.atlas", TextureAtlas.class));
		_skin.add("default-font", _app._font);
		_skin.load(Gdx.files.internal("ui/uiskin.json"));

		initNavigationButtons();
		initGrid();
		
		_infoLabel = new Label("Welcome!! Click any button to begin.", _skin);
		_infoLabel.setPosition(15, 310);
		_infoLabel.setAlignment(Align.center);
		_infoLabel.addAction(Actions.sequence(Actions.alpha(0f), Actions.delay(0.5f), Actions.fadeIn(0.5f)
				));
		_stage.addActor(_infoLabel);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update
		update(delta);

		_stage.draw();

		_app._batch.begin();
		_app._font.draw(_app._batch, "Play Screen", 20, 20);
		_app._batch.end();
	}

	private void update(float delta) {
		_stage.act(delta);

	}

	@Override
	public void resize(int width, int height) {
		System.out.println("PlayScreen::resize()");

	}

	@Override
	public void pause() {
		System.out.println("PlayScreen::pause()");

	}

	@Override
	public void resume() {
		System.out.println("PlayScreen::resume()");

	}

	@Override
	public void hide() {
		System.out.println("PlayScreen::hide()");

	}

	@Override
	public void dispose() {
		_stage.dispose();
		System.out.println("PlayScreen::dispose()");

	}

	private void initNavigationButtons() {
		_backButton = new TextButton("Back", _skin);
		_backButton.setPosition(20, _app._camera.viewportHeight - 60);
		_backButton.setSize(100f, 50f);
		_backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				_app.setScreen(_app._mainMenuScreen);
			}
		});

		_stage.addActor(_backButton);

	}

	private void initGrid() {
		
		Array<Integer> nums = new Array<Integer>();
		for(int i = 1 ; i < _boardSize * _boardSize ; ++i){
			nums.add(i);
		}
		nums.shuffle();
		_holdX = MathUtils.random(0, _boardSize -1);
		_holdY = MathUtils.random(0, _boardSize -1);

		_slideButtonGrid = new SlideButton[_boardSize][_boardSize];
		for (int i = 0; i < _boardSize; ++i) {
			for (int j = 0; j < _boardSize; ++j) {
				if (i != _holdY || j != _holdX) {

					//int id = j + 1 + (_boardSize * i);
					int id = nums.removeIndex(0);
					_slideButtonGrid[i][j] = new SlideButton(id + "", _skin, "default", id);
					_slideButtonGrid[i][j].setPosition(_app._camera.viewportWidth / 7 * 2 + 51 * j,
							_app._camera.viewportHeight / 5 * 3 - 51 * i);
					_slideButtonGrid[i][j].setSize(50, 50);
					_slideButtonGrid[i][j].addAction(
							Actions.sequence(Actions.alpha(0), Actions.delay((float) (j + 1 + (_boardSize * i)) / 20f), Actions.parallel(
									Actions.fadeIn(0.5f), Actions.moveBy(-10f, 0.25f, 0.5f, Interpolation.pow5Out))));
					
					_slideButtonGrid[i][j].addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent e, float x, float y) {
							// Slide the button
							int buttonX  = 0, buttonY = 0;
							boolean buttonFound = false;
							SlideButton selectedButton = (SlideButton) e.getListenerActor();
							for(int i = 0 ; i < _boardSize && !buttonFound; ++i ){
								for(int j = 0 ; j < _boardSize  && !buttonFound; ++j ){
									if(_slideButtonGrid[i][j] != null && selectedButton == _slideButtonGrid[i][j]){
										buttonX = j ;
										buttonY = i;
										buttonFound = true;
									}
								}
							}
							
							// After finding the button clicked by user
							if(_holdX == buttonX || _holdY == buttonY){
								//System.out.println("Button Found: " + selectedButton.getId() + " " + buttonX + "," + buttonY);
								moveButtons(buttonX, buttonY);
								if(isSolutionFound()){
									System.out.println("Solution Found....");
								}
							}else{
								_infoLabel.clearActions();
								_infoLabel.setText("Invalid Move!!");
								_infoLabel.addAction(Actions.sequence(
										Actions.alpha(1f), Actions.delay(1f), Actions.fadeOut(0.5f)));
							}
							
						}
					});
					
					_stage.addActor(_slideButtonGrid[i][j]);
				}
			}
		}
	}

	protected void moveButtons(int x, int y) {
		SlideButton button;
		if(x < _holdX ){
			for(; _holdX > x ; --_holdX){
				button = _slideButtonGrid[_holdY][_holdX - 1 ];
				button.addAction(Actions.moveBy(51, 0, 0.5f, Interpolation.pow5Out));
				_slideButtonGrid[_holdY][_holdX] = button;
				_slideButtonGrid[_holdY][_holdX - 1] = null;
			}
		}else{
			for(; _holdX < x ; ++_holdX){
				button = _slideButtonGrid[_holdY][_holdX + 1 ];
				button.addAction(Actions.moveBy(-51, 0, 0.5f, Interpolation.pow5Out));
				_slideButtonGrid[_holdY][_holdX] = button;
				_slideButtonGrid[_holdY][_holdX + 1] = null;
			}
		}
		if(y < _holdY ){
			for(; _holdY > y ; --_holdY){
				button = _slideButtonGrid[_holdY - 1][_holdX];
				button.addAction(Actions.moveBy(0, -51, 0.5f, Interpolation.pow5Out));
				_slideButtonGrid[_holdY][_holdX] = button;
				_slideButtonGrid[_holdY - 1][_holdX] = null;
			}
		}else{
			for(; _holdY < y ; ++_holdY){
				button = _slideButtonGrid[_holdY + 1][_holdX];
				button.addAction(Actions.moveBy(0, 51, 0.5f, Interpolation.pow5Out));
				_slideButtonGrid[_holdY][_holdX] = button;
				_slideButtonGrid[_holdY + 1][_holdX] = null;
			}
		}
	}
	
	private boolean isSolutionFound(){
		int idCheck = 1;
		for(int i = 0 ; i < _boardSize ; ++i){
			for(int j  = 0 ; j < _boardSize ; ++j){
				if(_slideButtonGrid[i][j] != null){
					if(_slideButtonGrid[i][j].getId() == idCheck++){
						if(idCheck == 16){
							return true;
						}
					}
				}else{
					return false;
				}
			}
		}
		return false;
	}

}
