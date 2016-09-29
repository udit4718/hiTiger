package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/19/16.
 */
public class GetOpportunityDetailRequest extends HitigerRequest {
    @SerializedName("oppId")
    private long oppId;

    public GetOpportunityDetailRequest(String uniqueId, long userId, long oppId) {
        super(uniqueId, userId);
        this.oppId = oppId;
    }
}
