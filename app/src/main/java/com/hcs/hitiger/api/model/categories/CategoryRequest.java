package com.hcs.hitiger.api.model.categories;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

import java.util.List;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class CategoryRequest extends HitigerRequest{
    @SerializedName("catId")
    private long catId;
    @SerializedName("clubIds")
    private List<Long> clubIds;

    public CategoryRequest(String uniqueId, long userId, long catId) {
        super(uniqueId,userId);
        this.catId = catId;
    }

    public CategoryRequest(String uniqueId, long userId, long catId, List<Long> clubIds) {
        super(uniqueId,userId);
        this.catId = catId;
        this.clubIds = clubIds;
    }
}
