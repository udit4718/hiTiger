package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class UserFullProfile extends HitigerResponse {
    @SerializedName("user")
    private UserApiResponse user;
    @SerializedName("clubs")
    private List<Sport> sports;

    public UserApiResponse getUser() {
        return user;
    }

    public List<Sport> getSports() {
        return sports;
    }
}
