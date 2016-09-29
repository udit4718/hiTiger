package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class UserVenueResponse extends HitigerResponse {
    @SerializedName("venues")
    private List<VenueResponse> mVenues;

    public List<VenueResponse> getVenues() {
        return mVenues;
    }
}
