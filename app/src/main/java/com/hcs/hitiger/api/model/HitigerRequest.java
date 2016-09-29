package com.hcs.hitiger.api.model;

import com.google.gson.annotations.SerializedName;

public class HitigerRequest {

    @SerializedName("uniqueId")
    private String uniqueId;
    @SerializedName("userId")
    private long userId;

    public HitigerRequest(String uniqueId, long userId) {
        this.uniqueId = uniqueId;
        this.userId = userId;
    }

    public HitigerRequest(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
