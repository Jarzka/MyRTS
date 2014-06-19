package org.voimala.myrts.screens.gameplay.input.commands;

public abstract class AbstractRTSCommandUnit extends AbstractRTSCommand {

    private long id;

    protected AbstractRTSCommandUnit(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
