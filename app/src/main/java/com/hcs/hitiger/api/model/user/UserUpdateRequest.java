package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class UserUpdateRequest extends HitigerRequest {
    @SerializedName("user")
    private UserRequest user;

    public UserRequest getUser() {
        return user;
    }

    public UserUpdateRequest(String uniqueId, UserRequest user) {
        super(uniqueId);
        this.user = user;
    }
}
