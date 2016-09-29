package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/20/16.
 */
public class EventResponse extends HitigerResponse{
    @SerializedName("events")
    List<OpportunityApiResponse> events;

    public List<OpportunityApiResponse> getEvents() {
        return events;
    }
}
