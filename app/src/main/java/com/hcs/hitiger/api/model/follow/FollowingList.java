package com.hcs.hitiger.api.model.follow;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by sanyamtyagi on 15/06/16.
 */
public class FollowingList extends HitigerRequest{


    @SerializedName("appUserId")
    private long appUserId;
    public FollowingList(String uniqueId, long userId,long appUserId) {
        super(uniqueId, userId);
        this.appUserId=appUserId;
    }
}
