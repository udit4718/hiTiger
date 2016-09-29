package com.hcs.hitiger.api.model.notification;

import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by hot cocoa on 26/05/16.
 */
public class SubscribeNotification extends HitigerRequest {

    private String deviceId;
    private String regId;

    public SubscribeNotification(String uniqueId, long userId, String deviceId, String regId) {
        super(uniqueId, userId);
        this.deviceId = deviceId;
        this.regId = regId;
    }

}
