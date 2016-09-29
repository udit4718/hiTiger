package com.hcs.hitiger.api.model.pubnub;

import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.background.GcmStartPublishChatMessageService;
import com.hcs.hitiger.database.ChatMessageDao;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hot cocoa on 20/05/16.
 */
public class PubnubMessageHelper {

    private static final String TAG = PubnubMessageHelper.class.getCanonicalName();

    public static PubnubEnvelope getPubNubEnvelope(Object message) {
        try {
            if (message instanceof JSONObject) {
                if (((JSONObject) message).has("message")) {
                    message = ((JSONObject) message).get("message");
                }
                if (message instanceof JSONObject && ((JSONObject) message).has("pn_gcm")) {
                    JSONObject pnGcm = (JSONObject) ((JSONObject) message).get("pn_gcm");
                    JSONObject data = (JSONObject) pnGcm.get("data");
                    message = data.get("message");
                }
            }
            PubnubEnvelope pubnubEnvelope = HitigerApplication.getInstance().getGson().fromJson(message.toString(), PubnubEnvelope.class);
            if(pubnubEnvelope.getContentType()==PubnubEnvelope.ContentType.MESSAGE.getValue()){
               return pubnubEnvelope;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void publishToChannel(final TextChatMessageApi textChatMessageApi, String channel) {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                Log.d(TAG, "publish successCallback() v1 returned: " + message);
                ChatMessageDao.getInstance().updateMessageSentStatusFlag(textChatMessageApi, channel.equals(HitigerApplication.getInstance().getUserDetail().getId() + ""));
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(HitigerApplication.getInstance());
                OneoffTask task = new OneoffTask.Builder()
                        .setService(GcmStartPublishChatMessageService.class)
                        .setTag(GcmStartPublishChatMessageService.CHAT_PUBLISH_SERVICE)
                        .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                        .setUpdateCurrent(true)
                        .build();
                gcmNetworkManager.schedule(task);
            }
        };
        if (!channel.equals(HitigerApplication.getInstance().getUserDetail().getId() + "")) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", textChatMessageApi.getPubnubEnevolpe().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PnGcmMessage pnGcmMessage = new PnGcmMessage(jsonObject);
            PnMessage pnMessage = new PnMessage(HitigerApplication.getInstance().getPubnub(), channel, callback, null, pnGcmMessage);
        try {
            pnMessage.publish();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
        } else {
            HitigerApplication.getInstance().getPubnub().publish(channel, textChatMessageApi.getPubnubEnevolpe().toString(), callback);
        }
    }
}
