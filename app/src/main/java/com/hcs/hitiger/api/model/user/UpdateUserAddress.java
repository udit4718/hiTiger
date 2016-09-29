package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.HitigerResponse;

import retrofit.Callback;

/**
 * Created by sanyamtyagi on 14/06/16.
 */
public class UpdateUserAddress  {

    @SerializedName("uniqueId")
    private String uniqueId;
    @SerializedName("user")
    private UserRequest user;


    public UpdateUserAddress(String fbId, UserRequest user) {
        this.user=user;
        this.uniqueId=fbId;
    }

}
