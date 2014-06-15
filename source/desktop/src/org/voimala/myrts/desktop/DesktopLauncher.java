package org.voimala.myrts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.voimala.myrts.app.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "MyRTS";
        config.width = 1024;
        config.height = 680;
        /**
         * FPS in Libgdx means how many times per second the render method will be called.
         * Since render method also handles game logic and world updating, it should be called
         * as many times in second as possible.
         */
        config.backgroundFPS = 120;
        config.foregroundFPS = 120;
        config.vSyncEnabled = true; // Warning: affects game world update rate

		new LwjglApplication(GameMain.getInstance(), config);
	}
}
