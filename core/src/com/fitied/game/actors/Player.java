package com.fitied.game.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.fitied.game.Application;
import com.fitied.game.utils.Constants;

public class Player extends Box2dSprite {
	private Application _app;
	private int _coinCount;

	public Player(Application app, Body body) {
		super(body, 32, 32);
		_app = app;
		_coinCount = 0;
		Texture playerTexture = _app._assetManager.get("playerSprite.png");
		setFrameDimension(playerTexture.getWidth() / 4, playerTexture.getHeight() / 4);
		TextureRegion[] downSprites = TextureRegion.split(playerTexture, playerTexture.getWidth() / 4,
				playerTexture.getHeight() / 4)[0];
		setDownAnimation(downSprites, 1.0f);
		TextureRegion[] leftSprites = TextureRegion.split(playerTexture, playerTexture.getWidth() / 4,
				playerTexture.getHeight() / 4)[1];
		setBackwardAnimation(leftSprites, 1.0f);
		TextureRegion[] rightSprites = TextureRegion.split(playerTexture, playerTexture.getWidth() / 4,
				playerTexture.getHeight() / 4)[2];
		setForwardAnimation(rightSprites, 1.0f);
		TextureRegion[] upSprites = TextureRegion.split(playerTexture, playerTexture.getWidth() / 4,
				playerTexture.getHeight() / 4)[3];
		setUpwardAnimation(upSprites, 1.0f);
	}
	
	public void collectedCoin(){
		++_coinCount;
	}
	
	public int getCoinsCount(){
		return _coinCount;
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

}
