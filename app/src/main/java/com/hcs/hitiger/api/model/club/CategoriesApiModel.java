package com.hcs.hitiger.api.model.club;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.user.ClubApiModel;

import java.util.List;

/**
 * Created by anuj gupta on 5/11/16.
 */
public class CategoriesApiModel implements Parcelable{
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("clubs")
    private List<ClubApiModel> clubs;

    protected CategoriesApiModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoriesApiModel> CREATOR = new Creator<CategoriesApiModel>() {
        @Override
        public CategoriesApiModel createFromParcel(Parcel in) {
            return new CategoriesApiModel(in);
        }

        @Override
        public CategoriesApiModel[] newArray(int size) {
            return new CategoriesApiModel[size];
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
}
