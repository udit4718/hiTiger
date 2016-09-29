package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class OpportunityResponse extends HitigerResponse {
    @SerializedName("opportunity")
    private OpportunityApiResponse opportunity;

    public OpportunityApiResponse getOpportunity() {
        return opportunity;
    }
}
