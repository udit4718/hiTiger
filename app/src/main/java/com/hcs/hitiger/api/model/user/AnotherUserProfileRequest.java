package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/16/16.
 */
public class AnotherUserProfileRequest extends HitigerRequest {
    @SerializedName("myId")
    private long myId;

    public AnotherUserProfileRequest(String uniqueId, long myId, long userId) {
        super(uniqueId, userId);
        this.myId = myId;
    }
}
