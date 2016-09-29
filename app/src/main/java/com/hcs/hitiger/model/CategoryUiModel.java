package com.hcs.hitiger.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hcs.hitiger.api.model.club.CategoriesApiModel;
import com.hcs.hitiger.api.model.user.ClubApiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/11/16.
 */
public class CategoryUiModel implements Parcelable{
    private long id;
    private String name;
    private String image;
    private List<ClubApiModel> clubs;

    public CategoryUiModel(long id, String name, String image, List<ClubApiModel> clubs) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.clubs = clubs;
    }

    protected CategoryUiModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<CategoryUiModel> CREATOR = new Creator<CategoryUiModel>() {
        @Override
        public CategoryUiModel createFromParcel(Parcel in) {
            return new CategoryUiModel(in);
        }

        @Override
        public CategoryUiModel[] newArray(int size) {
            return new CategoryUiModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public List<ClubApiModel> getClubs() {
        return clubs;
    }

    public static List<CategoryUiModel> getCategoryList(List<CategoriesApiModel> categoriesApiModelList) {
        List<CategoryUiModel> categoryUiModelList = new ArrayList<>();
        for (int i = 0; i < categoriesApiModelList.size(); i++) {
            categoryUiModelList.add(new CategoryUiModel(categoriesApiModelList.get(i).getId(), categoriesApiModelList.get(i).getName(),
                    categoriesApiModelList.get(i).getImage(), categoriesApiModelList.get(i).getClubs()));
        }
        return categoryUiModelList;
    }

    public static List<CategoryUiModel> getCategoryWithFilteredCLubListList(List<CategoriesApiModel> categoriesApiModelList) {
        List<CategoryUiModel> categoryUiModelList = new ArrayList<>();
        for (int i = 0; i < categoriesApiModelList.size(); i++) {
            if (categoriesApiModelList.get(i).getClubs().size() > 0)
                categoryUiModelList.add(new CategoryUiModel(categoriesApiModelList.get(i).getId(), categoriesApiModelList.get(i).getName(),
                        categoriesApiModelList.get(i).getImage(), categoriesApiModelList.get(i).getClubs()));
        }
        return categoryUiModelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(image);
    }
}
