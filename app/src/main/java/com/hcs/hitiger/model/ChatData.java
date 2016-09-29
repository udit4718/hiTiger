package com.hcs.hitiger.model;

/**
 * Created by anuj gupta on 5/17/16.
 */
public class ChatData {
    private String message;
    private boolean isSender;

    public ChatData(String message, boolean isSender) {
        this.message = message;
        this.isSender = isSender;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSender() {
        return isSender;
    }
}
