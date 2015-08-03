package com.fitied.game.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fitied.game.utils.Animation;
import com.fitied.game.utils.Constants;

public class Box2dSprite extends Actor{
	
	public enum Direction {
		RIGHT,
		LEFT,
		UP,
		DOWN
	}
	
	protected Body _body;
	protected Animation _forwardAnimation;
	protected Animation _backwardAnimation;
	protected Animation _downAnimation;
	protected Animation _upAnimation;
	protected float _width;
	protected float _height;
	private Direction _currDirection;
	
	public Box2dSprite(Body body, float w, float h) {
		_body = body;
		_forwardAnimation = new Animation();
		_backwardAnimation = new Animation();
		_upAnimation = new Animation();
		_downAnimation = new Animation();
		_width = w;
		_height = h;
	}
	public void setBody(Body b){
		_body = b;
	}
	
	public void setDirection(Direction d){
		_currDirection = d;
	}
	
	public Direction getDirection(){
		return _currDirection;
	}
	
	public void setFrameDimension(float w, float h){
		_width = w;
		_height = h;
	}
	
	public void setForwardAnimation(TextureRegion[] region, float delay){
		_forwardAnimation.setFrames(region, delay);
		System.out.println("Frames: " + region.length + " Width: " + _width + " Height: " + _height); 
	}
	public void setBackwardAnimation(TextureRegion[] region, float delay){
		_backwardAnimation.setFrames(region, delay);
		System.out.println("Frames: " + region.length + " Width: " + _width + " Height: " + _height); 
	}
	public void setUpwardAnimation(TextureRegion[] region, float delay){
		_upAnimation.setFrames(region, delay);
		System.out.println("Frames: " + region.length + " Width: " + _width + " Height: " + _height); 
	}
	public void setDownAnimation(TextureRegion[] region, float delay){
		_downAnimation.setFrames(region, delay);
		System.out.println("Frames: " + region.length + " Width: " + _width + " Height: " + _height); 
	}
	
	public void forwardAnimation(float dt){
		_forwardAnimation.update(dt);
	}
	
	public void backwardAnimation(float dt){
		_backwardAnimation.update(dt);
	}
	
	public void upAnimation(float dt){
		_upAnimation.update(dt);
	}
	
	public void downAnimation(float dt){
		_downAnimation.update(dt);
	}
	

	public void update(float dt){
		//_animation.update(dt);
	}
	
	public void render(SpriteBatch sb){
		sb.begin();
		switch (_currDirection) {
		case RIGHT:
			sb.draw(_forwardAnimation.getCurrFrame(), 
					_body.getPosition().x * Constants.M2P - _width/2, 
					_body.getPosition().y * Constants.M2P - _height/2);
			break;
		case LEFT:
			sb.draw(_backwardAnimation.getCurrFrame(), 
					_body.getPosition().x * Constants.M2P - _width/2, 
					_body.getPosition().y * Constants.M2P - _height/2);
			break;
		case UP:
			sb.draw(_upAnimation.getCurrFrame(), 
					_body.getPosition().x * Constants.M2P - _width/2, 
					_body.getPosition().y * Constants.M2P - _height/2);
			break;
		case DOWN:
			sb.draw(_downAnimation.getCurrFrame(), 
					_body.getPosition().x * Constants.M2P - _width/2, 
					_body.getPosition().y * Constants.M2P - _height/2);
			break;
		default:
			break;
		}
		sb.end();
	}
	
	public Body getBody() {
		return _body;
	}
	
	public Vector2 getPosition() {
		return _body.getPosition();
	}
	
}
