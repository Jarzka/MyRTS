package org.voimala.myrts.screens.loadgameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.AbstractGameScreen;
import org.voimala.myrts.screens.ScreenName;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public class LoadGameScreen extends AbstractGameScreen {

    private LoadGameThread loadGameThread;
    private WorldController worldController;

    private boolean isEverythingLoaded = false;

    private SpriteBatch hudBatch;
    private BitmapFont defaultFont;

    public LoadGameScreen() {
        initializeLoadingScreen();
    }

    private void initializeLoadingScreen() {
        initializeBatch();
        initializeFonts();
    }

    private void initializeFonts() {
        defaultFont = new BitmapFont();
        defaultFont.setColor(Color.WHITE);
    }

    private void initializeBatch() {
        hudBatch = new SpriteBatch();
    }

    @Override
    public void render(float deltaTime) {
        update();
        renderLoadingScreen();
    }

    private void update() {
        startLoadGameThreadIfNotStarted();
        checkLoadGameThreadState();
    }

    private void startLoadGameThreadIfNotStarted() {
        if (loadGameThread == null) {
            loadGameThread = new LoadGameThread(this);
            loadGameThread.start();
        }
    }

    private void checkLoadGameThreadState() {
        if (worldController != null) {
            isEverythingLoaded = true;
            GameMain.getInstance().setNextScreen(ScreenName.GAMEPLAY);
        }
    }

    private void renderLoadingScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (loadGameThread != null) {
            hudBatch.begin();
            defaultFont.draw(hudBatch,
                    loadGameThread.getLoadState() + "(" + loadGameThread.getLoadProcess() + "%)",
                    10,
                    Gdx.graphics.getHeight() - 10);
            hudBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    /** Note: Only LoadGameThread should call this method. */
    public void setWorldController(final WorldController worldController) {
        this.worldController = worldController;
    }

    public WorldController getWorldController() {
        return worldController;
    }

    public boolean isEverythingLoaded() {
        return isEverythingLoaded;
    }

}
