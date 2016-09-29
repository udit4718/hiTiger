package com.hcs.hitiger.api.model.categories;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.user.ClubApiModel;

import java.util.List;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class CategoryResponse extends HitigerResponse {
    @SerializedName("clubs")
    private List<ClubApiModel> clubs;

    public List<ClubApiModel> getClubs() {
        return clubs;
    }
}
