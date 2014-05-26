package org.voimala.myrts.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import org.voimala.myrts.multiplayer.NetworkManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;

import java.util.HashMap;

public class CommandLineParser {

    private static final String TAG = CommandLineParser.class.getName();
    private HashMap<String, String> commandLineArguments = new HashMap<String, String>();
    private static CommandLineParser instanceOfThis = null;
    private GameMain gameMain = null;

    private CommandLineParser() {}

    public static CommandLineParser getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new CommandLineParser();
        }

        return instanceOfThis;
    }

    public void setGameMain(GameMain gameMain) {
        this.gameMain = gameMain;
    }

    public void saveCommandLineArguments(String[] commandLineArguments) {
        for (int i = 0; i < commandLineArguments.length; i = i + 2) {
            try {
                this.commandLineArguments.put(commandLineArguments[i], commandLineArguments[i + 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
    }


    public void handleCommandLineArguments() {
        if (commandLineArguments.get("-multiplayer") != null) {
            if (commandLineArguments.get("-multiplayer").equals("host")) {
                gameMain.startGame(GameplayStartMethod.MULTIPLAYER_HOST);
            } else if (commandLineArguments.get("-multiplayer").equals("join")) {
                gameMain.startGame(GameplayStartMethod.MULTIPLAYER_JOIN);
            }
        }
    }

    public HashMap<String, String> getCommandLineArguments() {
        return commandLineArguments;
    }


}
