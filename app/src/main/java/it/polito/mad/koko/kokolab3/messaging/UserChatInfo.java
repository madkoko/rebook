package it.polito.mad.koko.kokolab3.messaging;

import java.io.Serializable;
import java.util.Map;

import it.polito.mad.koko.kokolab3.profile.Profile;

/**
 * Created by Francesco on 17/05/2018.
 */

public class UserChatInfo implements Serializable {
    private String secondPartyId;
    private String secondPartyUsername;
    private String secondPartyImage;
    private String secondPartyToken;
    private String lastMessage;

    public UserChatInfo(){}

    public UserChatInfo(String secondPartyId, String secondPartyUsername, String secondPartyImage, String lastMessage, String secondPartyToken) {
        this.secondPartyId = secondPartyId;
        this.secondPartyUsername = secondPartyUsername;
        this.secondPartyImage = secondPartyImage;
        this.secondPartyToken = secondPartyToken;
        this.lastMessage = lastMessage;
    }

    /**
     * Constructor using the other user's profile object
     * @param secondPartyProfile   the second party profile object
     */
    public UserChatInfo(String secondPartyId, Profile secondPartyProfile, String lastMessage) {
        this.secondPartyId = secondPartyId;
        this.secondPartyUsername = secondPartyProfile.getName();
        this.secondPartyImage = secondPartyProfile.getImage();
        this.secondPartyToken = secondPartyProfile.getTokenMessage();
        this.lastMessage = lastMessage;
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

    public String getSecondPartyToken() {
        return secondPartyToken;
    }

    public void setSecondPartyToken(String secondPartyToken) {
        this.secondPartyToken = secondPartyToken;
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
                ", secondPartyToken='" + secondPartyToken + '\''+
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }
}
