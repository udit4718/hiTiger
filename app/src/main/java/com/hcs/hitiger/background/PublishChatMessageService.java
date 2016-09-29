package com.hcs.hitiger.background;

import android.app.IntentService;
import android.content.Intent;

import com.hcs.hitiger.api.model.pubnub.PubnubMessageHelper;
import com.hcs.hitiger.database.ChatMessageDao;
import com.hcs.hitiger.database.model.ChatMessageDb;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class PublishChatMessageService extends IntentService {

    public PublishChatMessageService() {
        super("ChatMessageSyncService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        List<ChatMessageDb> messageList = ChatMessageDao.getInstance().getUnPublishedMessageList();
//        todo publish message serially not concurrently.
        for (ChatMessageDb chatMessage : messageList) {
            if (chatMessage.getPublishStatus() == ChatMessageDb.MessageStatus.NOT_SENT.getValue()) {
                PubnubMessageHelper.publishToChannel(chatMessage.createChatMeesageApi(), chatMessage.getSenderId() + "");
                PubnubMessageHelper.publishToChannel(chatMessage.createChatMeesageApi(), chatMessage.getRecieverId() + "");
            } else if (chatMessage.getPublishStatus() == ChatMessageDb.MessageStatus.SENT_TO_OWN_CHANNEL.getValue()) {
                PubnubMessageHelper.publishToChannel(chatMessage.createChatMeesageApi(), chatMessage.getRecieverId() + "");
            } else if (chatMessage.getPublishStatus() == ChatMessageDb.MessageStatus.SENT_TO_RECIEVER_CHANNEL.getValue()) {
                PubnubMessageHelper.publishToChannel(chatMessage.createChatMeesageApi(), chatMessage.getSenderId() + "");
            }
        }
    }

}
