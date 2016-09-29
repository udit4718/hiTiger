package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by SajalHCS on 6/2/16.
 */
public class CancelRequestToPlayRequest extends HitigerRequest {
    @SerializedName("opportunityId")
    private long opportunityId;

    @SerializedName("requesterId")
    private long requesterId;

    public CancelRequestToPlayRequest(String uniqueId, long userId, long opportunityId, long requesterId) {
        super(uniqueId, userId);
        this.opportunityId = opportunityId;
        this.requesterId = requesterId;
    }

    public long getOpportunityId() {
        return opportunityId;
    }

    public long getRequesterId() {
        return requesterId;
    }
}
