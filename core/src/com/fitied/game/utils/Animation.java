package com.fitied.game.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {

	private TextureRegion[] _frames;
	private float _time;
	private float _delay;
	private int _currFrame;
	private int _timesPlayed;

	public Animation() {

	}

	public Animation(TextureRegion[] frames) {
		this(frames, 1 / 12f);
	}

	public Animation(TextureRegion[] frames, float delay) {
		setFrames(frames, delay);
	}

	public void setFrames(TextureRegion[] frames, float delay) {
		_frames = frames;
		_delay = delay;
		_time = 0;
		_currFrame = 0;
		_timesPlayed = 0;
	}

	public void update(float dt) {
		if (_delay <= 0) {
			return;
		}
		_time += dt;
		while (_time >= _delay) {
			step();
		}
	}

	public void singleSetAnimation(float dt) {
		_currFrame++;
		if (_currFrame == _frames.length) {
			_currFrame = 0;
			_timesPlayed++;
		}
	}

	private void step() {
		_time -= _delay;

		_currFrame++;
		if (_currFrame == _frames.length) {
			_currFrame = 0;
			_timesPlayed++;
		}
	}

	public TextureRegion getCurrFrame() {
		return _frames[_currFrame];
	}

	public int getTimesPlayed() {
		return _timesPlayed;
	}

}
