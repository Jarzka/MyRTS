package org.voimala.myrts.networking;

public class ChatMessage {

    private String author = "";
    private String message = "";
    private long timeStamp = 0;

    public ChatMessage(final String author, final String message, final long timeStamp) {
        this.author = author;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
