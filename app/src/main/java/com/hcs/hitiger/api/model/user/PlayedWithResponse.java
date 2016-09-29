package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/23/16.
 */
public class PlayedWithResponse extends HitigerResponse{
    @SerializedName("users")
    private List<UserApiResponse> users;

    public List<UserApiResponse> getUsers() {
        return users;
    }
}
