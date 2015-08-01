package com.fitied.game.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.fitied.game.Application;
import com.fitied.game.utils.Animation;

public class Coin extends Box2dSprite {
	private Application _app;
	private Animation _animation;
	public Coin(Application app, Body body) {
		super(body, 32, 32);
		_app = app;
		Texture coinTexture = _app._assetManager.get("coinSprite.png");
		_animation = new Animation();
		setFrameDimension(coinTexture.getWidth() / 8, coinTexture.getHeight() / 1);
		TextureRegion[] sprites = TextureRegion.split(coinTexture, coinTexture.getWidth() / 8,
				coinTexture.getHeight() / 1)[0];
		setForwardAnimation(sprites, 1.0f);
	}

}
