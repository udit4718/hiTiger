package com.hcs.hitiger.api.model.club;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.user.ClubApiModel;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class CreateCategoryResponse extends HitigerResponse {
    @SerializedName("club")
    private ClubApiModel club;

    public ClubApiModel getclub() {
        return club;
    }
}
