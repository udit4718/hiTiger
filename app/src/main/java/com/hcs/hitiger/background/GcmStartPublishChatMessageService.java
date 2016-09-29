package com.hcs.hitiger.background;

import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by hot cocoa on 25/05/16.
 */
public class GcmStartPublishChatMessageService extends GcmTaskService {

    public static final String CHAT_PUBLISH_SERVICE = "chat_publish_service";

    @Override
    public int onRunTask(TaskParams taskParams) {
        startService(new Intent(this, PublishChatMessageService.class));
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
