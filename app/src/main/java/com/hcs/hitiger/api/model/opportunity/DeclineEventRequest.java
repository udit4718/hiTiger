package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by SajalHCS on 6/2/16.
 */
public class DeclineEventRequest extends HitigerRequest {
    @SerializedName("oppId")
    private long oppId;

    public DeclineEventRequest(String uniqueId, long userId, long oppId) {
        super(uniqueId, userId);
        this.oppId = oppId;
    }
}
