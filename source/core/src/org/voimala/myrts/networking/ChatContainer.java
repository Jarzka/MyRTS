package org.voimala.myrts.networking;

import java.util.ArrayList;

public class ChatContainer {

    private ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

    private static ChatContainer instanceOfThis;
    private long latestMessageReceivedTimestamp = 0;

    private ChatContainer() {}

    public static ChatContainer getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new ChatContainer();
        }

        return instanceOfThis;
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    /** Returns the newest chat messages in the following format:
     * Author: message
     * The first string in the array contains the newest chat message, the second index contains
     * the second newest message and so on.
     */
    public String[] getNewestChatMessagesAsStrings(int maxNumberOfMessages) {
        ArrayList<String> messages = new ArrayList<String>();

        for (int i = 0; i < chatMessages.size(); i++) {
            if (chatMessages.size() - i > maxNumberOfMessages) {
                continue;
            }

            if (chatMessages.get(i).getAuthor().toLowerCase().equals("server")) {
                StringBuilder constructMessage = new StringBuilder();
                constructMessage.append(chatMessages.get(i).getMessage());
                messages.add(constructMessage.toString());
            } else {
                StringBuilder constructMessage = new StringBuilder();
                constructMessage.append(chatMessages.get(i).getAuthor());
                constructMessage.append(": ");
                constructMessage.append(chatMessages.get(i).getMessage());
                messages.add(constructMessage.toString());
            }
        }

        String[] messagesArray = new String[messages.size()];
        for (int i = 0; i < messagesArray.length; i++) {
            messagesArray[i] = messages.get(i);
        }

        return messagesArray;
    }

    public void clearAllChatMessages() {
        chatMessages.clear();
    }

    public void addChatMessage(final ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        latestMessageReceivedTimestamp = System.currentTimeMillis();
    }

    public long getMillisecondsPassedSinceLastMessageReceived() {
        return System.currentTimeMillis() - latestMessageReceivedTimestamp;
    }

}
