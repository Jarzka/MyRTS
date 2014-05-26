package org.voimala.myrts.app;

import java.util.HashMap;

public class CommandLineParser {

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

    public HashMap<String, String> getCommandLineArguments() {
        return commandLineArguments;
    }


}
