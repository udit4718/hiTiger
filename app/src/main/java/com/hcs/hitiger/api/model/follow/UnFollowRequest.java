package com.hcs.hitiger.api.model.follow;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/16/16.
 */
public class UnFollowRequest extends HitigerRequest {
    @SerializedName("followingId")
    private long followingId;

    public UnFollowRequest(String uniqueId, long userId, long followingId) {
        super(uniqueId, userId);
        this.followingId = followingId;
    }
}
