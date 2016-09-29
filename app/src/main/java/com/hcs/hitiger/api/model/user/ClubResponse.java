package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class ClubResponse extends HitigerResponse {
    @SerializedName("club")
    private ClubApiModel club;
    @SerializedName("users")
    private List<UserApiResponse> users;
    @SerializedName("opportunities")
    private long opportunities;
    @SerializedName("added")
    private boolean added;

    public ClubApiModel getClub() {
        return club;
    }

    public List<UserApiResponse> getUsers() {
        return users;
    }

    public long getOpportunities() {
        return opportunities;
    }

    public boolean isAdded() {
        return added;
    }
}
