package it.polito.mad.koko.kokolab3.messaging;

import java.util.Map;

/**
 * Created by Francesco on 17/05/2018.
 */

public class UserChatInfo {
    private String secondPartyId;
    private String secondPartyUsername;
    private String secondPartyImage;
    private String lastMessage;

    public UserChatInfo(){}

    public UserChatInfo(String secondPartyId, String secondPartyUsername, String secondPartyImage,String lastMessage) {
        this.secondPartyId = secondPartyId;
        this.secondPartyUsername = secondPartyUsername;
        this.secondPartyImage = secondPartyImage;
        this.lastMessage=lastMessage;
    }

    public String getSecondPartyId() {
        return secondPartyId;
    }

    public void setSecondPartyId(String secondPartyId) {
        this.secondPartyId = secondPartyId;
    }

    public String getSecondPartyUsername() {
        return secondPartyUsername;
    }

    public void setSecondPartyUsername(String secondPartyUsername) {
        this.secondPartyUsername = secondPartyUsername;
    }

    public String getSecondPartyImage() {
        return secondPartyImage;
    }

    public void setSecondPartyImage(String secondPartyImage) {
        this.secondPartyImage = secondPartyImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "UserChatInfo{" +
                "secondPartyId='" + secondPartyId + '\'' +
                ", secondPartyUsername='" + secondPartyUsername + '\'' +
                ", secondPartyImage='" + secondPartyImage + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }
}
