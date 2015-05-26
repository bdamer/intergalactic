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
        config.width = 960;
        config.height = 540;
        config.resizable = false;        
        //config.width = 1366;
        //config.height = 768;
        //config.fullscreen = true;
        config.title = "Intergalactic";
		new LwjglApplication(new IntergalacticGame(), config);
	}
}
