package com.fitied.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SlideButton extends TextButton {

	private int _id;
	
	public SlideButton(String text, Skin skin, String styleName, int id) {
		super(text, skin, styleName);
		_id = id;
	}
	
	public int getId() {
		return _id;
	}

}
