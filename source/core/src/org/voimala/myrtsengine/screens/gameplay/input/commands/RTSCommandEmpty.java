package org.voimala.myrtsengine.screens.gameplay.input.commands;

import com.badlogic.gdx.math.Vector2;

public class RTSCommandEmpty extends AbstractRTSCommand {

    public RTSCommandEmpty(final int playerWhoMadeCommand) {
        super(playerWhoMadeCommand);
    }

    @Override
    public RTSCommandType getCommandName() {
        return RTSCommandType.EMPTY;
    }
}
