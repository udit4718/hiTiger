package com.hcs.hitiger.model;

import com.hcs.hitiger.api.model.user.ClubApiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/3/16.
 */
public class CategoryData {
    private long id;
    private String imageUrl;
    private String name;
    private long categoryId;
    private boolean added;

    public CategoryData(long id, String imageUrl, String name, long categoryId, boolean added) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.categoryId = categoryId;
        this.added = added;
    }

    public static List<CategoryData> getCategories(List<ClubApiModel> clubApiModelList) {
        List<CategoryData> categoryDataList = new ArrayList<>();
        for (int i = 0; i < clubApiModelList.size(); i++) {
            categoryDataList.add(new CategoryData(clubApiModelList.get(i).getId(), clubApiModelList.get(i).getImageUrl(),
                    clubApiModelList.get(i).getName(), clubApiModelList.get(i).getCategoryId(), clubApiModelList.get(i).isAdded()));
        }
        return categoryDataList;
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

    public void setAdded(boolean added) {
        this.added = added;
    }
}
