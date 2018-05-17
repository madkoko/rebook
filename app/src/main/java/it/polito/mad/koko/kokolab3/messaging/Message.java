package it.polito.mad.koko.kokolab3.messaging;

import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Francesco on 11/05/2018.
 */

public class Message implements Serializable{

    private String timestamp;
    private String sender;
    private String text;
    private String check;

    public Message(){
        this.check="false";
    };

    public Message(String timestamp, String sender, String text) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.text = text;
        this.check="false";
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timestamp=" + timestamp +
                ", sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", check='" + check + '\''+
                '}';
    }

}

