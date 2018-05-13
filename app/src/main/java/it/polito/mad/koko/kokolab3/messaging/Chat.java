package it.polito.mad.koko.kokolab3.messaging;

import java.util.ArrayList;

/**
 * Created by Francesco on 12/05/2018.
 */

public class Chat {

    private ArrayList<Message> chatMessages;

    public Chat(){};

    public Chat(ArrayList<Message> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public ArrayList<Message> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<Message> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatMessages=" + chatMessages +
                '}';
    }
}
