package it.polito.mad.koko.kokolab3.messaging;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Francesco on 12/05/2018.
 */

public class Chat {

    private String chatID;
    private ArrayList<Message> chatMessages;

    // private UserChatInfo secondParty; // PERCHè no?!?!

    public Chat(){};

    public Chat(String chatID,ArrayList<Message> chatMessages) {
        this.chatID = chatID;
        this.chatMessages = chatMessages;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public ArrayList<Message> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<Message> chatMessages) { this.chatMessages = chatMessages; }

    // Searching in Lists
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Chat)) return false;
        Chat otherChat = (Chat)o;
        return	chatID.equals(otherChat.chatID);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatID=" + chatID +
                "chatMessages=" + chatMessages +
                '}';
    }
}
