package com.hcs.hitiger.api.model.club;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/11/16.
 */
public class CategoriesResponse extends HitigerResponse {
    @SerializedName("categories")
    private List<CategoriesApiModel> categories;

    public List<CategoriesApiModel> getCategories() {
        return categories;
    }
}
