package com.hcs.hitiger.api.model.follow;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.user.UserApiResponse;

import java.util.List;

/**
 * Created by SajalHCS on 5/30/16.
 */
public class FollowingResponse extends HitigerResponse {
    @SerializedName("users")
    private List<UserApiResponse> users;

    public List<UserApiResponse> getUsers() {
        return users;
    }
}
