package com.fitied.game.utils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {

	private int _playerFootTouch = 0;
	Array<Body> _bodiesToRemoveFromWorld;
	
	public MyContactListener() {
		_bodiesToRemoveFromWorld = new Array<Body>();
	}
	
	public Array<Body> getBodiesToRemoveFromWorld(){
		return _bodiesToRemoveFromWorld;
	}
	
	public boolean isPlayerJumping(){
		return _playerFootTouch <= 0;
	}

	@Override
	public void beginContact(Contact contact) {

		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();

		if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
			_playerFootTouch++;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
			_playerFootTouch++;
		}
		if (fa.getUserData() != null && fa.getUserData().equals("coin")) {
			System.out.println("Coin touched by player.");
			_bodiesToRemoveFromWorld.add(fa.getBody());
		}
		if (fb.getUserData() != null && fb.getUserData().equals("coin")) {
			System.out.println("Coin touched by player.");
			_bodiesToRemoveFromWorld.add(fb.getBody());
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();

		if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
			_playerFootTouch--;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
			_playerFootTouch--;
		}

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
