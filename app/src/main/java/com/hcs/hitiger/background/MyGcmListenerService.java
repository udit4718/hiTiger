
package com.hcs.hitiger.background;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonSyntaxException;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.activities.ChatActivity;
import com.hcs.hitiger.api.model.pubnub.PubnubEnvelope;
import com.hcs.hitiger.api.model.pubnub.PubnubMessageHelper;
import com.hcs.hitiger.api.model.pubnub.TextChatMessageApi;
import com.hcs.hitiger.database.ChatMessageDao;
import com.hcs.hitiger.database.NotificationMessageDao;
import com.hcs.hitiger.database.model.ChatMessageDb;
import com.hcs.hitiger.database.model.NotificationMessageDb;
import com.hcs.hitiger.util.NotificationHelper;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "GCm Message: " + message);
        if (data.get("fromHiTiger") != null) {
            try {
                NotificationMessageDb notificationMessageDb =
                        HitigerApplication.getInstance().getGson().fromJson(data.getString("stringData"), NotificationMessageDb.class);
                NotificationMessageDao.getInstance().insert(notificationMessageDb);
                NotificationHelper.showUserActivityNotification(getApplicationContext(), notificationMessageDb);
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                Crashlytics.logException(ex);
            }
        } else {
            PubnubEnvelope pubnubEnvelope = PubnubMessageHelper.getPubNubEnvelope(message);
            if (pubnubEnvelope != null) {
                TextChatMessageApi chatMessageApi = HitigerApplication.getInstance().getGson().fromJson(pubnubEnvelope.getData(), TextChatMessageApi.class);
                ChatMessageDb chatMessageDb = chatMessageApi.createChatMessageDb();
                if (ChatMessageDao.getInstance().insertMessage(chatMessageDb)) {
                    if (HitigerApplication.getInstance().getmChatPageEventId() == chatMessageDb.getEventId()) {
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ChatActivity.NEW_MESSAGE_FETCHED));
                    } else {
                        NotificationHelper.showChatMessagesNotification(getApplicationContext(), chatMessageDb);
                    }
                }
            }
        }
    }


}
