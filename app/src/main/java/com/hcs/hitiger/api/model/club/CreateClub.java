package com.hcs.hitiger.api.model.club;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class CreateClub {
    @SerializedName("name")
    private String name;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("categoryId")
    private long categoryId;

    public CreateClub(String name, String imageUrl, long categoryId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getCategoryId() {
        return categoryId;
    }
}
