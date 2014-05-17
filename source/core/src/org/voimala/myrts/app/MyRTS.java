package org.voimala.myrts.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.voimala.myrts.gameplay.WorldController;
import org.voimala.myrts.gameplay.WorldRenderer;
import org.voimala.myrts.multiplayer.ClientThread;
import org.voimala.myrts.multiplayer.ServerThread;

import java.util.HashMap;

public class MyRTS extends ApplicationAdapter {
    private static final String TAG = MyRTS.class.getName();
    private WorldController worldController;
    private WorldRenderer worldRenderer;
    private boolean paused = false;
    private ServerThread serverThread;
    private HashMap<String, String> commandLineArguments = new HashMap<String, String>();
    private ClientThread clientThread;

    public MyRTS(String[] commandLineArguments) {
        super();
        saveCommandLineArguments(commandLineArguments);
    }

    private void saveCommandLineArguments(String[] commandLineArguments) {
        for (int i = 0; i < commandLineArguments.length; i = i + 2) {
            try {
                this.commandLineArguments.put(commandLineArguments[i], commandLineArguments[i + 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        handleCommandLineArguments();

        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);
    }

    private void handleCommandLineArguments() {
        if (commandLineArguments.get("-multiplayer").equals("host")) {
            hostGame();
            joinGame();
        } else if (commandLineArguments.get("-multiplayer").equals("join")) {
            joinGame();
        }
    }

    private void joinGame() {
        clientThread = new ClientThread(
                commandLineArguments.get("-ip"),
                Integer.valueOf(commandLineArguments.get("-port")));
        clientThread.start();
    }

    private void hostGame() {
        ServerThread server = new ServerThread(
                Integer.valueOf(commandLineArguments.get("-port")));
        server.start();
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
        if (clientThread != null) {
            clientThread.die();
        }

        if (serverThread != null) {
            serverThread.die();
        }

        worldRenderer.dispose();
    }
}
