package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by sanyamtyagi on 20/06/16.
 */
public class CloseEvent extends HitigerRequest {

    @SerializedName("eventId")
    private long eventId;

    public CloseEvent(String uniqueId, long userId,long eventId) {
        super(uniqueId, userId);
        this.eventId = eventId;
    }
}
