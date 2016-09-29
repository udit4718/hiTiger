package com.hcs.hitiger.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class AddressData implements Parcelable{
    private long id;
    private String address;
    private String access;
    private double lat;
    private double lng;

    public AddressData(long id, String address, String access, double lat, double lng) {
        this.id = id;
        this.address = address;
        this.access = access;
        this.lat = lat;
        this.lng = lng;
    }

    public AddressData(String address) {
        this.address = address;
    }

    protected AddressData(Parcel in) {
        id = in.readLong();
        address = in.readString();
        access = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<AddressData> CREATOR = new Creator<AddressData>() {
        @Override
        public AddressData createFromParcel(Parcel in) {
            return new AddressData(in);
        }

        @Override
        public AddressData[] newArray(int size) {
            return new AddressData[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getAccess() {
        return access;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(address);
        dest.writeString(access);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
