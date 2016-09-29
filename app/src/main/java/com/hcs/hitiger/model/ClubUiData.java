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
public class ClubUiData implements Parcelable {
    private long id;
    private String name;
    private String imageUrl;
    private long categoryId;
    private CategoriesApiModel category;

    public ClubUiData(long id, String name, String imageUrl, long categoryId, CategoriesApiModel category) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.category = category;
    }

    protected ClubUiData(Parcel in) {
        id = in.readLong();
        name = in.readString();
        imageUrl = in.readString();
        categoryId = in.readLong();
        category = in.readParcelable(CategoriesApiModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeLong(categoryId);
        dest.writeParcelable(category, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClubUiData> CREATOR = new Creator<ClubUiData>() {
        @Override
        public ClubUiData createFromParcel(Parcel in) {
            return new ClubUiData(in);
        }

        @Override
        public ClubUiData[] newArray(int size) {
            return new ClubUiData[size];
        }
    };

    public long getId() {
        return id;
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

    public CategoriesApiModel getCategory() {
        return category;
    }

    public static List<ClubUiData> getList(List<ClubApiModel> clubs) {
        List<ClubUiData> clubUiDataList = new ArrayList<>();
        for (int i = 0; i < clubs.size(); i++) {
            clubUiDataList.add(new ClubUiData(clubs.get(i).getId(), clubs.get(i).getName(), clubs.get(i).getImageUrl(),
                    clubs.get(i).getCategoryId(), clubs.get(i).getCategory()));
        }
        return clubUiDataList;
    }
}
