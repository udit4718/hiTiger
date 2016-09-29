package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/18/16.
 */
public class SendRequestToPlayRequest {
    @SerializedName("opportunityId")
    private long opportunityId;
    @SerializedName("requesterId")
    private long requesterId;
    @SerializedName("uniqueId")
    private String uniqueId;

    public SendRequestToPlayRequest(String uniqueId, long opportunityId, long requesterId) {
        this.uniqueId = uniqueId;
        this.opportunityId = opportunityId;
        this.requesterId = requesterId;
    }
}
