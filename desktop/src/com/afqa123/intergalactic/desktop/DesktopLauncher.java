package com.afqa123.intergalactic.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.afqa123.intergalactic.IntergalacticGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // Disable frame-rate limit
        //config.foregroundFPS = 0;
        //config.backgroundFPS = 0;        
		new LwjglApplication(new IntergalacticGame(), config);
	}
}
