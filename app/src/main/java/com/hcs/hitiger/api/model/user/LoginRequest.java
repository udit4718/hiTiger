package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/18/16.
 */
public class LoginRequest {
    @SerializedName("id")
    private String id;

    public LoginRequest(String id) {
        this.id = id;
    }
}
