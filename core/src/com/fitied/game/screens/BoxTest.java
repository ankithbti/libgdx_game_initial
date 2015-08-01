package com.fitied.game.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.fitied.game.Application;
import com.fitied.game.actors.Box2dSprite;
import com.fitied.game.actors.Coin;
import com.fitied.game.actors.Player;
import com.fitied.game.utils.Constants;

import sun.lwawt.macosx.CPrinterDevice;

public class BoxTest implements Screen, InputProcessor {

	private final Application _app;
	private Player _playerSprite;
	public Array<Coin> _coinSprites;
	Vector3 _camPosDelta;
	private Body _platform;
	private Body _fakePlatform;
	private float _playerSpeed;

	private TiledMap _map;
	private OrthoCachedTiledMapRenderer _mapRenderer;
	private float _tileSize;

	private Sound _jumpSound;
	private Sound _coinCollectedSound;
	private Music _backgroundMusic;

	private float _stateTime;

	private static final int NUM_KEYS = 3;

	private boolean _currentKeys[];
	private boolean _prevKeys[];

	private final static int BUTTON_LEFT = 0;
	private final static int BUTTON_RIGHT = 1;
	private final static int BUTTON_UP = 2;

	public BoxTest(final Application app) {
		_app = app;
		_playerSpeed = 2.0f;
		_camPosDelta = new Vector3(0, 0, 0);
		_currentKeys = new boolean[NUM_KEYS];
		_prevKeys = new boolean[NUM_KEYS];
		for (int i = 0; i < NUM_KEYS; ++i) {
			_currentKeys[i] = false;
			_prevKeys[i] = false;
		}

		_map = new TmxMapLoader().load("mario_level_1.tmx");
		_mapRenderer = new OrthoCachedTiledMapRenderer(_map);

		// createTileMapGround();
		// createTileMapWall();

		// Read Object Layer for GroundBounds Layer
		createGroundBounds();

		_coinSprites = new Array<Coin>();

	}

	@Override
	public void show() {

		_playerSprite = new Player(_app, createPlayer(200, 400, 45, 80, false, true, false, Constants.TYPE_BOX,
				(short) (Constants.TYPE_PLATFORM | Constants.TYPE_WALL | Constants.TYPE_COINS), (short) 0));
		_playerSprite.setDirection(Box2dSprite.Direction.RIGHT);

		// Get Coins for Coins Layer
		createCoins();

		_jumpSound = _app._assetManager.get("sound/mario_jump.wav", Sound.class);
		_backgroundMusic = _app._assetManager.get("music/mario_backgroundMusic.mp3", Music.class);
		_coinCollectedSound = _app._assetManager.get("sound/mario_coin.wav", Sound.class);

		_backgroundMusic.setVolume(0.5f);
		Gdx.input.setInputProcessor(this);
		// _backgroundMusic.play();
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		_stateTime += Gdx.graphics.getDeltaTime();
		update(Gdx.graphics.getDeltaTime());
		if (_stateTime >= Constants.STEP) {
			_stateTime -= Constants.STEP;

			// Draw Tile Map
			_mapRenderer.setView(_app._camera);
			_mapRenderer.render();
			_app._batch.setProjectionMatrix(_app._camera.combined);
			// Make the Camera to follow the player
			_app._camera.position
					.set(new Vector3(Math.max(_app.VIEWPORT_WIDTH / 2, _playerSprite.getPosition().x * Constants.M2P),
							_app._camera.position.y, 0));

			_app._camera.update();
			_app._box2dCam.position
					.set(new Vector3(Math.max(_playerSprite.getPosition().x, _app.VIEWPORT_WIDTH / 2 * Constants.P2M),
							_app._box2dCam.position.y, 0));
			_app._box2dCam.update();

			// Draw Coins
			for(Coin c : _coinSprites){
				c.render(_app._batch);
			}

			// Draw Player
			_playerSprite.render(_app._batch);

			// Draw Text
			_app._batch.setProjectionMatrix(_app._textCamera.combined);
			_app._batch.begin();
			_app._font.setColor(Color.GREEN);
			_app._font.draw(_app._batch, " FPS: " + Gdx.graphics.getFramesPerSecond() + " , Coins: " + _playerSprite.getCoinsCount(), 10,
					400);
			_app._batch.end();
		}

	}
	
	public void incrementCoinCollectedCount(){
		_playerSprite.collectedCoin();
		// Play Sound
		_coinCollectedSound.play(0.5f);
	}

	private void update(float delta) {

		for (Coin c : _coinSprites) {
			c.setDirection(Box2dSprite.Direction.RIGHT);
			c.forwardAnimation(1 / 10f);
		}
		// Update the position of player using INPUT controls
		float displacement = 1 * delta * _playerSpeed;
		_camPosDelta.x = 0;
		if (_currentKeys[BUTTON_RIGHT]) {
			_playerSprite.setDirection(Box2dSprite.Direction.RIGHT);
			Vector2 pos = _playerSprite.getBody().getPosition();
			_playerSprite.getBody().setTransform(new Vector2(pos.x + displacement, pos.y), 0.0f);
			_playerSprite.forwardAnimation(1 / 10f);
			_playerSprite.getBody().setAwake(true);
			_camPosDelta.x += 1 * delta * _playerSpeed;
		}
		if (_currentKeys[BUTTON_LEFT]) {
			_playerSprite.setDirection(Box2dSprite.Direction.LEFT);
			Vector2 pos = _playerSprite.getBody().getPosition();
			_playerSprite.getBody().setTransform(new Vector2(pos.x - displacement, pos.y), 0.0f);
			_playerSprite.backwardAnimation(1 / 10f);
			_playerSprite.getBody().setAwake(true);
			_camPosDelta.x -= 1 * delta * _playerSpeed;
		}
		if (_currentKeys[BUTTON_UP]) {
			if (!_app._contactListener.isPlayerJumping()) {
				_playerSprite.setDirection(Box2dSprite.Direction.UP);
				_playerSprite.getBody().applyForceToCenter(0, 1000.0f, true);
				_playerSprite.upAnimation(1 / 10f);
				new Thread(new Runnable() {

					@Override
					public void run() {

						// _jumpSound.play(0.5f);

					}
				}).start();
			}
		}

	}

	@Override
	public void resize(int width, int height) {
		System.out.println("BoxTest::resize()");

	}

	@Override
	public void pause() {
		System.out.println("BoxTest::pause()");
		// _backgroundMusic.pause();
	}

	@Override
	public void resume() {
		System.out.println("BoxTest::resume()");
		// _backgroundMusic.play();

	}

	@Override
	public void hide() {
		System.out.println("BoxTest::hide()");
		_backgroundMusic.stop();
	}

	@Override
	public void dispose() {
		_mapRenderer.dispose();
		_map.dispose();
		_jumpSound.dispose();
		_backgroundMusic.dispose();
		System.out.println("BoxTest::dispose()");

	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.RIGHT) {
			_currentKeys[BUTTON_RIGHT] = true;
			// _playerBox.applyForceToCenter(100.0f, 0, true);
		}
		if (keycode == Input.Keys.LEFT) {
			_currentKeys[BUTTON_LEFT] = true;
			// _playerBox.applyForceToCenter(-100.0f, 0, true);
		}
		if (keycode == Input.Keys.SPACE) {
			_currentKeys[BUTTON_UP] = true;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.RIGHT) {
			_currentKeys[BUTTON_RIGHT] = false;
		} else if (keycode == Input.Keys.LEFT) {
			_currentKeys[BUTTON_LEFT] = false;
		} else if (keycode == Input.Keys.SPACE) {
			_currentKeys[BUTTON_UP] = false;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	private Body createPlayer(float x, float y, float w, float h, boolean isStatic, boolean fixedRotation,
			boolean isSensor, short categoryFilterBits, short maskFilterBits, short groupIndex) {
		Body body;

		BodyDef bodyDef = new BodyDef();
		if (isStatic) {
			bodyDef.type = BodyType.StaticBody;
		} else {
			bodyDef.type = BodyType.DynamicBody;
		}
		bodyDef.fixedRotation = fixedRotation;
		bodyDef.position.set(x * Constants.P2M, y * Constants.P2M);

		body = _app._world.createBody(bodyDef);
		body.setUserData(this);

		FixtureDef bodyFix = new FixtureDef();

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(w / 2 * Constants.P2M, h / 2 * Constants.P2M);

		bodyFix.shape = shape;
		// bodyFix.restitution = 1.0f;
		bodyFix.density = 1.0f;
		bodyFix.friction = 0.0f;
		bodyFix.isSensor = isSensor;
		bodyFix.filter.categoryBits = categoryFilterBits;
		bodyFix.filter.maskBits = maskFilterBits;
		bodyFix.filter.groupIndex = groupIndex;

		body.createFixture(bodyFix).setUserData("player");

		shape.setAsBox(w / 2 * Constants.P2M, 5 * Constants.P2M, new Vector2(0, -h / 2 * Constants.P2M), 0.0f);
		bodyFix.shape = shape;
		bodyFix.isSensor = true; // Foot Sensor
		bodyFix.filter.categoryBits = Constants.TYPE_FOOT;
		bodyFix.filter.maskBits = (short) (Constants.TYPE_PLATFORM | Constants.TYPE_WALL);
		bodyFix.filter.groupIndex = groupIndex;

		body.createFixture(bodyFix).setUserData("foot");

		return body;
	}

	private void createTileMapWall() {
		// Get Ground from TMX file
		TiledMapTileLayer layer = (TiledMapTileLayer) _map.getLayers().get("Wall");
		if (layer == null) {
			return;
		}
		_tileSize = layer.getTileWidth(); // Width and Height are same 32x32
		// Draw Box2d Ground for it
		for (int row = 0; row < layer.getHeight(); ++row) {
			for (int col = 0; col < layer.getWidth(); ++col) {

				// Get the cell - Going Column wise [ Downwards in tile image ]
				Cell cell = layer.getCell(col, row);

				// Check if cell has any tile or not
				if (cell == null) {
					continue;
				}
				if (cell.getTile() == null) {
					continue;
				}

				Body _wallBody;

				// Create a Body from this tile of cell
				// Get the vertices and draw Box in Box2d
				BodyDef def = new BodyDef();
				def.type = BodyType.StaticBody;
				def.position.set((col + 0.5f) * _tileSize * Constants.P2M, (row + 0.5f) * _tileSize * Constants.P2M); // As

				// Nice for Ground Bodies
				ChainShape cs = new ChainShape();
				// Get vertices of the Cell
				Vector2[] v = new Vector2[2];

				v[0] = new Vector2(-_tileSize / 2 * Constants.P2M, _tileSize / 2 * Constants.P2M);
				v[1] = new Vector2(_tileSize / 2 * Constants.P2M, _tileSize / 2 * Constants.P2M);
				cs.createChain(v);

				FixtureDef fix = new FixtureDef();
				fix.friction = 1.0f;
				fix.shape = cs;
				fix.filter.categoryBits = Constants.TYPE_WALL;
				fix.filter.maskBits = (short) (Constants.TYPE_BOX | Constants.TYPE_FOOT);
				_wallBody = _app._world.createBody(def);

				_wallBody.createFixture(fix).setUserData("upper-wall");

				// Nice for Ground Bodies
				ChainShape csBottom = new ChainShape();
				// Get vertices of the Cell
				Vector2[] bottomV = new Vector2[2];

				bottomV[0] = new Vector2(-_tileSize / 2 * Constants.P2M, -_tileSize / 2 * Constants.P2M);
				bottomV[1] = new Vector2(_tileSize / 2 * Constants.P2M, -_tileSize / 2 * Constants.P2M);
				csBottom.createChain(bottomV);

				fix.friction = 1.0f;
				fix.shape = csBottom;
				fix.filter.categoryBits = Constants.TYPE_WALL;
				fix.filter.maskBits = (short) (Constants.TYPE_BOX);
				_wallBody.createFixture(fix).setUserData("lower-wall");
			}
		}

	}

	private void createTileMapGround() {
		// Get Ground from TMX file
		TiledMapTileLayer layer = (TiledMapTileLayer) _map.getLayers().get("Ground");
		_tileSize = layer.getTileWidth(); // Width and Height are same 32x32
		// Draw Box2d Ground for it
		for (int row = 0; row < layer.getHeight(); ++row) {
			for (int col = 0; col < layer.getWidth(); ++col) {

				// Get the cell - Going Column wise [ Downwards in tile image ]
				Cell cell = layer.getCell(col, row);

				// Check if cell has any tile or not
				if (cell == null) {
					continue;
				}
				if (cell.getTile() == null) {
					continue;
				}
				// Create a Body from this tile of cell
				// Get the vertices and draw Box in Box2d
				BodyDef def = new BodyDef();
				def.type = BodyType.StaticBody;
				def.position.set((col + 0.5f) * _tileSize * Constants.P2M, (row + 0.5f) * _tileSize * Constants.P2M); // As

				// Nice for Ground Bodies
				ChainShape cs = new ChainShape();
				// Get vertices of the Cell
				Vector2[] v = new Vector2[3];
				v[0] = new Vector2(-_tileSize / 2 * Constants.P2M, _tileSize / 2 * Constants.P2M);
				v[1] = new Vector2(_tileSize / 2 * Constants.P2M, _tileSize / 2 * Constants.P2M);
				v[2] = new Vector2(_tileSize / 2 * Constants.P2M, -_tileSize / 2 * Constants.P2M);
				cs.createChain(v);

				FixtureDef fix = new FixtureDef();
				fix.friction = 0.1f;
				fix.shape = cs;
				fix.filter.categoryBits = Constants.TYPE_PLATFORM;
				fix.filter.maskBits = (short) (Constants.TYPE_BOX | Constants.TYPE_FOOT);
				_app._world.createBody(def).createFixture(fix).setUserData("ground");
			}
		}

	}

	private void createGroundBounds() {
		MapLayer layer = (MapLayer) _map.getLayers().get("GroundBounds");
		if (layer == null) {
			return;
		}
		Body body;
		BodyDef def = new BodyDef();
		FixtureDef fix = new FixtureDef();
		Shape shape;
		for (MapObject mo : layer.getObjects()) {

			if (mo instanceof PolylineMapObject) {
				shape = createPolyLine((PolylineMapObject) mo);
			} else {
				continue;
			}

			def.type = BodyType.StaticBody;
			body = _app._world.createBody(def);

			fix.density = 1.0f;
			fix.shape = shape;
			fix.filter.categoryBits = Constants.TYPE_PLATFORM;
			fix.filter.categoryBits = (short) (Constants.TYPE_PLATFORM | Constants.TYPE_FOOT);

			body.createFixture(fix).setUserData("ground");
		}
	}

	private Shape createPolyLine(PolylineMapObject polyLine) {
		float[] vertices = polyLine.getPolyline().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];
		for (int i = 0; i < vertices.length / 2; ++i) {
			worldVertices[i] = new Vector2(vertices[i * 2] * Constants.P2M, vertices[i * 2 + 1] * Constants.P2M);
		}
		ChainShape cs = new ChainShape();
		cs.createChain(worldVertices);
		return cs;
	}

	private void createCoins() {
		MapLayer layer = (MapLayer) _map.getLayers().get("Coins");
		if (layer == null) {
			return;
		}
		System.out.println("Got Coins Layer...");
		Body body;
		BodyDef def = new BodyDef();
		FixtureDef fix = new FixtureDef();
		Shape shape;
		for (MapObject mo : layer.getObjects()) {

			float circleObjectX = (Float) mo.getProperties().get("x") * Constants.P2M;
			float circleObjectY = (Float) mo.getProperties().get("y") * Constants.P2M;

			def.type = BodyType.StaticBody;
			def.position.x = circleObjectX;
			def.position.y = circleObjectY;
			body = _app._world.createBody(def);

			Coin coin = new Coin(_app, body);
			body.setUserData(coin);

			fix.density = 1.0f;
			CircleShape cs = new CircleShape();
			cs.setRadius(10 * Constants.P2M);
			fix.shape = cs;
			fix.isSensor = true;
			fix.filter.categoryBits = Constants.TYPE_COINS;
			fix.filter.maskBits = (short) (Constants.TYPE_BOX);

			body.createFixture(fix).setUserData("coin");

			_coinSprites.add(coin);
		}
	}
}
