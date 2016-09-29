package com.hcs.hitiger.database.model;

import com.hcs.hitiger.api.model.pubnub.ChatMessage;
import com.hcs.hitiger.api.model.pubnub.TextChatMessageApi;

/**
 * Created by hot cocoa on 17/05/16.
 */
public class ChatMessageDb extends ChatMessage {


    private String message;
    private int publishStatus;
    private int status;  // 0 for unread and 1 for read

    public ChatMessageDb() {
    }

    public ChatMessageDb(String uniqueMessageId, long senderId, long recieverId, long eventId, String message, long timestamp, int publishStatus,int type,int status) {
        super(uniqueMessageId, senderId, recieverId, eventId, timestamp,type);
        this.message = message;
        this.publishStatus = publishStatus;
        this.status = status;
    }

    public enum MessageStatus {
        NOT_SENT(0),
        SENT_TO_OWN_CHANNEL(1),
        SENT_TO_RECIEVER_CHANNEL(2),
        SENT_TO_BOTH_CHANNEL(3);

        MessageStatus(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    public String getMessage() {
        return message;
    }

    public int getPublishStatus() {
        return publishStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPublishStatus(int publishStatus) {
        this.publishStatus = publishStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public TextChatMessageApi createChatMeesageApi() {
        return new TextChatMessageApi(uniqueMessageId, senderId, recieverId, eventId, message, timestamp,type);
    }
}
