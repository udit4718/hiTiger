package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.club.CategoriesApiModel;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class ClubApiModel {
    @SerializedName("id")
    private long id;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("name")
    private String name;
    @SerializedName("categoryId")
    private long categoryId;
    @SerializedName("added")
    private boolean added;
    @SerializedName("category")
    private CategoriesApiModel category;

    public CategoriesApiModel getCategory() {
        return category;
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public boolean isAdded() {
        return added;
    }
}
