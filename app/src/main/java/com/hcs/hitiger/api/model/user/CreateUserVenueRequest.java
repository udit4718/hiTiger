package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class CreateUserVenueRequest extends HitigerRequest{
    @SerializedName("venue")
    private VenueRequest venue;

    public CreateUserVenueRequest(String uniqueId, VenueRequest venue) {
        super(uniqueId);
        this.venue = venue;
    }
}
