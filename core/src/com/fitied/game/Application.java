package com.fitied.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader.FreeTypeFontGeneratorParameters;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fitied.game.actors.Coin;
import com.fitied.game.screens.BoxTest;
import com.fitied.game.screens.LoadingScreen;
import com.fitied.game.screens.MainMenuScreen;
import com.fitied.game.screens.PlayScreen;
import com.fitied.game.screens.SplashScreen;
import com.fitied.game.utils.Constants;
import com.fitied.game.utils.MyContactListener;

public class Application extends Game {

	public static final String TITLE = "MYGAME";
	public static final float M2P = 30.0f;
	public static final float P2M = 1 / M2P;
	public static final float TIME_ITERATION = 1 / 60.0f;
	public static final int VIEWPORT_WIDTH = 480;
	public static final int VIEWPORT_HEIGHT = 420;

	public OrthographicCamera _camera;
	public OrthographicCamera _box2dCam;
	public OrthographicCamera _textCamera;
	public SpriteBatch _batch;
	public AssetManager _assetManager;
	public BitmapFont _font;

	public LoadingScreen _loadingScreen;
	public SplashScreen _splashScreen;
	public MainMenuScreen _mainMenuScreen;
	public PlayScreen _playScreen;
	public BoxTest _box2dScreen;

	public World _world;
	public Box2DDebugRenderer _worldDebugRenderer;
	public MyContactListener _contactListener;
	public final static boolean isDebugDraw = false;
	private float _accum;

	@Override
	public void create() {
		System.out.println("Creating Application");
		_world = new World(new Vector2(0, -9.81f), true);
		_worldDebugRenderer = new Box2DDebugRenderer();
		_contactListener = new MyContactListener();
		_world.setContactListener(_contactListener);

		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		_textCamera = new OrthographicCamera();
		_textCamera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		_box2dCam = new OrthographicCamera();
		_box2dCam.setToOrtho(false, VIEWPORT_WIDTH * Constants.P2M, VIEWPORT_HEIGHT * Constants.P2M);
		_batch = new SpriteBatch();
		_assetManager = new AssetManager();
		_loadingScreen = new LoadingScreen(this);
		_splashScreen = new SplashScreen(this);
		_mainMenuScreen = new MainMenuScreen(this);
		_playScreen = new PlayScreen(this);
		_box2dScreen = new BoxTest(this);

		initFonts();

		this.setScreen(_loadingScreen);

	}

	@Override
	public void render() {
		_accum += Gdx.graphics.getDeltaTime();
		if (_accum >= Constants.STEP) {
			_accum -= Constants.STEP;
			update(_accum);
			super.render(); // This is IMP for screen2d Game in libgdx
			if (isDebugDraw) {
				_worldDebugRenderer.render(_world, _box2dCam.combined);
			}
		}
	}

	private void update(float dt) {
		_world.step(Constants.STEP, 8, 5);

		// Now remove the unwanted bodies from world
		Array<Body> toRem = _contactListener.getBodiesToRemoveFromWorld();
		for (int i = 0; i < toRem.size ; ++i) {
			Body b = toRem.get(i);
			_box2dScreen._coinSprites.removeValue((Coin)b.getUserData(), true);
			_world.destroyBody(b);
			_box2dScreen.incrementCoinCollectedCount();
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					_box2dScreen.incrementCoinCollectedCount();
//				}
//			});
		}
		toRem.clear();
	}

	@Override
	public void dispose() {
		_loadingScreen.dispose();
		// _splashScreen.dispose();
		// _mainMenuScreen.dispose();
		// _playScreen.dispose();
		_box2dScreen.dispose();
		_batch.dispose();
		_assetManager.dispose();

		System.out.println("Disposing Application");
		super.dispose();
	}

	private void initFonts() {
		// FreeTypeFontGenerator _fontGenerator =
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Amatic-Bold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

		params.size = 35;
		params.color = Color.BLACK;
		_font = fontGenerator.generateFont(params);
	}

}
