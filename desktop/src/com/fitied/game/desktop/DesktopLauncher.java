package com.fitied.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fitied.game.Application;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Application.TITLE;
		config.width = Application.VIEWPORT_WIDTH;
		config.height = Application.VIEWPORT_HEIGHT;
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		config.resizable = true;
		new LwjglApplication(new Application(), config);
	}
}
