package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/17/16.
 */
public class GetOpportunity extends HitigerResponse{
    @SerializedName("opportunities")
    private List<OpportunityApiResponse> opportunity;

    public List<OpportunityApiResponse> getOpportunity() {
        return opportunity;
    }
}
