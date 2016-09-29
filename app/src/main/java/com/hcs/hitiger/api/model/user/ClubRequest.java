package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class ClubRequest {
    @SerializedName("userId")
    private long userId;
    @SerializedName("clubId")
    private long clubId;
    @SerializedName("uniqueId")
    private String uniqueId;

    public ClubRequest(long userId, long clubId, String uniqueId) {
        this.userId = userId;
        this.clubId = clubId;
        this.uniqueId = uniqueId;
    }
}
