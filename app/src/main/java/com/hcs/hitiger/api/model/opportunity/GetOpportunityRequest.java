package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/17/16.
 */
public class GetOpportunityRequest extends HitigerRequest {
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("sportId")
    private long sportId;
    @SerializedName("date")
    private long date;

    public GetOpportunityRequest(String uniqueId, long userId, double lat, double lng) {
        super(uniqueId, userId);
        this.lat = lat;
        this.lng = lng;
    }

    public GetOpportunityRequest(String uniqueId, long userId, double lat, double lng, long sportId) {
        super(uniqueId, userId);
        this.lat = lat;
        this.lng = lng;
        this.sportId = sportId;
    }

    public GetOpportunityRequest(String uniqueId, long userId, double lat, double lng, long sportId, long date) {
        super(uniqueId, userId);
        this.lat = lat;
        this.lng = lng;
        this.sportId = sportId;
        this.date = date;
    }
}
