package com.hcs.hitiger.api.model.pubnub;

import com.hcs.hitiger.HitigerApplication;

/**
 * Created by hot cocoa on 19/05/16.
 */
public abstract class ChatMessage {

    protected String uniqueMessageId;
    protected long senderId;
    protected long recieverId;
    protected long eventId;
    protected long timestamp;
    protected int type;

    public ChatMessage() {
    }

    public ChatMessage(String uniqueMessageId, long senderId, long recieverId, long eventId, long timestamp, int type) {
        this.uniqueMessageId = uniqueMessageId;
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getUniqueMessageId() {
        return uniqueMessageId;
    }

    public void setUniqueMessageId(String uniqueMessageId) {
        this.uniqueMessageId = uniqueMessageId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(long recieverId) {
        this.recieverId = recieverId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSender() {
        if (senderId == HitigerApplication.getInstance().getUserDetail().getId()) {
            return true;
        }
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public enum MessageType {
        TEXT(1);
        private int value;

        public int getValue() {
            return value;
        }

        MessageType(int value) {
            this.value = value;
        }
    }

}
