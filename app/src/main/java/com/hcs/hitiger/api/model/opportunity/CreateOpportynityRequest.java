package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/11/16.
 */
public class CreateOpportynityRequest extends HitigerRequest {
    @SerializedName("opportunity")
    private OpportunityRequest opportunity;

    public CreateOpportynityRequest(String uniqueId, OpportunityRequest opportunity) {
        super(uniqueId);
        this.opportunity = opportunity;
    }

    public OpportunityRequest getOpportunity() {
        return opportunity;
    }
}
