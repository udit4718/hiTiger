package com.hcs.hitiger.api.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class VenueResponse implements Parcelable{
    @SerializedName("id")
    private long id;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("access")
    private String access;
    @SerializedName("creatorId")
    private int creatorId;

    protected VenueResponse(Parcel in) {
        id = in.readLong();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        access = in.readString();
        creatorId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(access);
        dest.writeInt(creatorId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VenueResponse> CREATOR = new Creator<VenueResponse>() {
        @Override
        public VenueResponse createFromParcel(Parcel in) {
            return new VenueResponse(in);
        }

        @Override
        public VenueResponse[] newArray(int size) {
            return new VenueResponse[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAccess() {
        return access;
    }

    public int getCreatorId() {
        return creatorId;
    }
}
