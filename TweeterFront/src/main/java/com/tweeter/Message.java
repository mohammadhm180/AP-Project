package com.tweeter;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String username;
    private String receiver;
    private String text;
    private LocalDateTime date;

    public Message(String username, String receiver, String text, LocalDateTime date) {
        this.username = username;
        this.receiver = receiver;
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getReceiver() {
        return receiver;
    }
}
