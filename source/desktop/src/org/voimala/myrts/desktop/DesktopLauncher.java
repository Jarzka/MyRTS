package org.voimala.myrts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.voimala.myrts.app.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "MyRTS";
        config.width = 800;
        config.height = 600;
        config.backgroundFPS = 80;
        config.foregroundFPS = 80;
        config.vSyncEnabled = true;
		new LwjglApplication(new GameMain(arg), config);
	}
}
