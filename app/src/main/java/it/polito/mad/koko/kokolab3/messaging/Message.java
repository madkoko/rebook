package it.polito.mad.koko.kokolab3.messaging;

import java.sql.Timestamp;

/**
 * Created by Francesco on 11/05/2018.
 */

public class Message {

    private String timestamp;
    private String sender;
    private String text;

    public Message(){};

    public Message(String timestamp, String sender, String text) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.text = text;
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

    @Override
    public String toString() {
        return "Message{" +
                "timestamp=" + timestamp +
                ", sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}

