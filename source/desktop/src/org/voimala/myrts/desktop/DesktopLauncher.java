package org.voimala.myrts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.voimala.myrts.app.GameMain;

import javax.swing.*;

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

        try {
            new LwjglApplication(GameMain.getInstance(), config);
        } catch (Exception e) {
            String errorMessage = "Serious error occurred and the execution can not continue." + "\n\n";
            errorMessage += "Error information" + ": " + "\n";
            errorMessage += e.toString() + "\n";
            errorMessage += "Call stack" + ": " + "\n";
            for (StackTraceElement element : e.getStackTrace()) {
                errorMessage += element.toString() + "\n";
            }

            JOptionPane.showMessageDialog(null, errorMessage);
            System.exit(0);
        }

	}
}
