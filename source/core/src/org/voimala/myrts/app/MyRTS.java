package org.voimala.myrts.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.voimala.myrts.gameplay.WorldController;
import org.voimala.myrts.gameplay.WorldRenderer;

public class MyRTS extends ApplicationAdapter {
    private static final String TAG = MyRTS.class.getName();
    private WorldController worldController;
    private WorldRenderer worldRenderer;
    private boolean paused = false;

    public MyRTS(String[] commandLineArguments) {
        super();

        // TODO Parse command line arguments
        for (String argument : commandLineArguments) {
            System.out.println(argument);
        }
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);
    }

    @Override
    public void render() {
        // Update game world
        if (!paused) {
            worldController.update(Gdx.graphics.getDeltaTime());
        }

        // Render frame
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
    }
}
