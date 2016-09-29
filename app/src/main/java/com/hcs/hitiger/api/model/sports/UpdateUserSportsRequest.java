package com.hcs.hitiger.api.model.sports;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

import java.util.List;

/**
 * Created by hot cocoa on 13/05/16.
 */
public class UpdateUserSportsRequest extends HitigerRequest{

    @SerializedName("clubIds")
    private List<Long> sportsIds;

    public UpdateUserSportsRequest(String uniqueId, long userId, List<Long> sportsIds) {
        super(uniqueId, userId);
        this.sportsIds = sportsIds;
    }
}
