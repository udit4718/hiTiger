package com.hcs.hitiger.api.model.follow;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/17/16.
 */
public class FollowRequest extends HitigerRequest{
    @SerializedName("userToFollowId")
    private long userToFollowId;

    public FollowRequest(String uniqueId, long userId, long userToFollowId) {
        super(uniqueId, userId);
        this.userToFollowId = userToFollowId;
    }
}
