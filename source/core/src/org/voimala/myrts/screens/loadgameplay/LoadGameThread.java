package org.voimala.myrts.screens.loadgameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.world.WorldRenderer;
import org.voimala.utility.MathHelper;

public class LoadGameThread extends Thread {

    private LoadGameScreen loadGameScreen;
    private float loadProcess = 0;
    private float loadProcessMax = 2; // How many times loadProcess++ is called
    private String loadState = "Initializing...";

    private WorldController preloadedWorldController;

    private boolean isEverythingLoaded = true;

    public LoadGameThread(final LoadGameScreen loadGameScreen) {
        this.loadGameScreen = loadGameScreen;
    }

    public void run() {
        initializeWorldController();
        finalizeLoading();
    }
    public void initializeWorldController() {
        loadState = "Initializing world...";
        preloadedWorldController = new WorldController();
        loadProcess++;
    }


    private void finalizeLoading() {
        loadGameScreen.setWorldController(preloadedWorldController);
        loadProcess++;
    }

    public String getLoadState() {
        return loadState;
    }

    public double getLoadProcess() {
        return MathHelper.round(loadProcess / loadProcessMax * 100, 0);
    }
}
