package org.voimala.myrts.exceptions;

public class UnableToHandleNetworkMessage extends RuntimeException {
    public UnableToHandleNetworkMessage(final String message) {
        super(message);
    }
}
