package com.hcs.hitiger.api.model.follow;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.user.UserApiResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/13/16.
 */
public class FollowersResponse extends HitigerResponse {
    @SerializedName("followers")
    private List<UserApiResponse> followers;

    public List<UserApiResponse> getFollowers() {
        return followers;
    }
}
