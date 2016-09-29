package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class User {
    @SerializedName("user")
    private UserRequest user;

    public User(UserRequest user) {
        this.user = user;
    }

    public UserRequest getUser() {
        return user;
    }
}
