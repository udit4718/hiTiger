package com.hcs.hitiger.api.model.pubnub;

/**
 * Created by hot cocoa on 20/05/16.
 */
public abstract class ChatMessageApi extends ChatMessage {

    public ChatMessageApi() {
    }

    public ChatMessageApi(String uniqueMessageId, long senderId, long recieverId, long eventId, long timestamp, int type) {
        super(uniqueMessageId, senderId, recieverId, eventId, timestamp, type);
    }

    abstract public PubnubEnvelope getPubnubEnevolpe();
}
