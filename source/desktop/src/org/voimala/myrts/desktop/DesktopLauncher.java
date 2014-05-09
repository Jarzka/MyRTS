package org.voimala.myrts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.voimala.myrts.app.MyRTS;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "MyRTS";
        config.width = 800;
        config.height = 600;
		new LwjglApplication(new MyRTS(), config);
	}
}
