package com.hcs.hitiger.api.model.pubnub;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.database.model.ChatMessageDb;

import java.util.Calendar;

/**
 * Created by hot cocoa on 16/05/16.
 */
public class TextChatMessageApi extends ChatMessageApi {

    private String message;

    public TextChatMessageApi(String uniqueMessageId, long senderId, long recieverId, long eventId, String message, long timestamp, int type) {
        super(uniqueMessageId, senderId, recieverId, eventId, timestamp, type);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return HitigerApplication.getInstance().getGsonBuilder().create().toJson(this);
    }

    public ChatMessageDb createChatMessageDb() {
        return new ChatMessageDb(uniqueMessageId, senderId, recieverId, eventId, message, Calendar.getInstance().getTimeInMillis(), ChatMessageDb.MessageStatus.SENT_TO_BOTH_CHANNEL.getValue(), type, 0);
    }

    @Override
    public PubnubEnvelope getPubnubEnevolpe() {
        return new PubnubEnvelope(this.toString(), PubnubEnvelope.ContentType.MESSAGE.getValue());
    }


}
