package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class UserResponseWithCode extends HitigerResponse {
    @SerializedName("user")
    UserApiResponse user;

    public UserApiResponse getUser() {
        return user;
    }
}
