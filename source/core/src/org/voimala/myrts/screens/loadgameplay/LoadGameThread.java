package org.voimala.myrts.screens.loadgameplay;

import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.world.WorldRenderer;

public class LoadGameThread extends Thread {

    private LoadGameScreen loadGameScreen;
    private float loadProcess = 0;
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
    }

    private void finalizeLoading() {
        loadGameScreen.setWorldController(preloadedWorldController);
    }

    public String getLoadState() {
        return loadState;
    }

    public float getLoadProcess() {
        return loadProcess;
    }
}
