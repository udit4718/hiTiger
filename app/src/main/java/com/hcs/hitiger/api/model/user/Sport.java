package com.hcs.hitiger.api.model.user;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.HitigerApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hot cocoa on 12/05/16.
 */
public class Sport implements Parcelable {
    private static final String SPORT_LIST = "SPORT_LIST";
    private static final String USER_SELECTED_SPORT_LIST = "USER_SELECTED_SPORT_LIST";
    private static final String MY_PREFS_NAME = "MY_PREFS_NAME";

    @SerializedName("id")
    private long id;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("name")
    private String name;
    @SerializedName("added")
    private boolean added;
    @SerializedName("color")
    private String color;

    // for local selection when creating opportunity
    private boolean locallySelected;


    protected Sport(Parcel in) {
        id = in.readLong();
        imageUrl = in.readString();
        name = in.readString();
        added = in.readByte() != 0;
        color = in.readString();
        locallySelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeByte((byte) (added ? 1 : 0));
        dest.writeString(color);
        dest.writeByte((byte) (locallySelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public boolean isAdded() {
        return added;
    }

    public String getColor() {
        return color;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public boolean isLocallySelected() {
        return locallySelected;
    }

    public void setLocallySelected(boolean locallySelected) {
        this.locallySelected = locallySelected;
    }

    public static List<Sport> getSportList() {
        List sports;
        SharedPreferences sharedPreferences = HitigerApplication.getInstance().getSharedPreferences();
        if (sharedPreferences.contains(SPORT_LIST)) {
            String jsonFavorites = sharedPreferences.getString(SPORT_LIST, null);
            Gson gson = new Gson();
            Sport[] favoriteItems = gson.fromJson(jsonFavorites, Sport[].class);
            sports = Arrays.asList(favoriteItems);
            sports = new ArrayList(sports);
        } else {
            return new ArrayList<Sport>();
        }
        return sports;
    }

    public static void setSportList(List<Sport> sportList) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = HitigerApplication.getInstance().getSharedPreferences();
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(sportList);
        editor.putString(SPORT_LIST, jsonFavorites);
        editor.commit();
    }

    public static List<Sport> getUserSelectedSportList(){
        List sports;
        SharedPreferences sharedPreferences = HitigerApplication.getInstance().getSharedPreferences();
        if (sharedPreferences.contains(USER_SELECTED_SPORT_LIST)) {
            String jsonFavorites = sharedPreferences.getString(USER_SELECTED_SPORT_LIST, null);
            Gson gson = new Gson();
            Sport[] favoriteItems = gson.fromJson(jsonFavorites, Sport[].class);
            sports = Arrays.asList(favoriteItems);
            sports = new ArrayList(sports);
        } else {
            return new ArrayList<Sport>();
        }
        return sports;
    }

    public static void setUserSelectedSportList(List<Sport> sportList) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = HitigerApplication.getInstance().getSharedPreferences();
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(sportList);
        editor.putString(USER_SELECTED_SPORT_LIST, jsonFavorites);
        editor.commit();
    }


}
