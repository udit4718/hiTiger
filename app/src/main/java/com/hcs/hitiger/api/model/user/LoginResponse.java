package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

/**
 * Created by anuj gupta on 5/18/16.
 */
public class LoginResponse extends HitigerResponse{
    @SerializedName("user")
    private UserApiResponse user;

    public UserApiResponse getUser() {
        return user;
    }
}
