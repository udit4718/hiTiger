package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class UserUniqueIdRequest extends HitigerRequest {
    @SerializedName("filterType")
    private String filterType;

    public UserUniqueIdRequest(String uniqueId) {
        super(uniqueId);
    }

    public UserUniqueIdRequest(String uniqueId, long userId) {
        super(uniqueId, userId);
    }

    public UserUniqueIdRequest(String uniqueId, long userId, String filterType) {
        super(uniqueId, userId);
        this.filterType = filterType;
    }
}
