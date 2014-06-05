package org.voimala.myrts.networking;

import java.util.ArrayList;

public class Chat {

    private ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

    private static Chat instanceOfThis;

    private Chat() {}

    public static Chat getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new Chat();
        }

        return instanceOfThis;
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    /** Returns the messages in the following format:
     * Author: message
     * */
    public String[] getChatMessagesForChatBox() {
        String[] messages = new String[Chat.getInstance().getChatMessages().size()];

        for (int i = 0; i < chatMessages.size(); i++) {
            StringBuilder constructMessage = new StringBuilder();
            constructMessage.append(chatMessages.get(i).getAuthor());
            constructMessage.append(": ");
            constructMessage.append(chatMessages.get(i).getMessage());

            messages[i] = constructMessage.toString();
        }

        return messages;
    }

    public void clearAllChatMessages() {
        chatMessages.clear();
    }

    public void addChatMessage(final ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
    }
}
