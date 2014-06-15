package org.voimala.myrts.app;

import java.util.HashMap;

public class CommandLineParser {

    private static final String TAG = CommandLineParser.class.getName();
    private HashMap<String, String> commandLineArguments = new HashMap<String, String>();
    private static CommandLineParser instanceOfThis = null;

    private CommandLineParser() {}

    public static CommandLineParser getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new CommandLineParser();
        }

        return instanceOfThis;
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
        /* These command line arguments are not used atm.
        if (commandLineArguments.get("-multiplayer") != null) {
            if (commandLineArguments.get("-multiplayer").equals("host")) {
                gameMain.setNextScreenToLoadGameplay(GameplayStartMethod.MULTIPLAYER);
            } else if (commandLineArguments.get("-multiplayer").equals("join")) {
                gameMain.setNextScreenToLoadGameplay(GameplayStartMethod.MULTIPLAYER_JOIN);
            }
        }
        */
    }

    public HashMap<String, String> getCommandLineArguments() {
        return commandLineArguments;
    }


}
