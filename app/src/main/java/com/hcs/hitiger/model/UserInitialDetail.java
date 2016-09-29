package com.hcs.hitiger.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anuj gupta on 4/27/16.
 */
public class UserInitialDetail implements Parcelable {
    private String fbId;
    private String fullName;
    private String emailId;
    private String imageUrl;
    private String phoneNumber;
    private double latitude;
    private double longitude;

    public UserInitialDetail(String fbId, String fullName, String emailId, String imageUrl, String phoneNumber) {
        this.fbId = fbId;
        this.fullName = fullName;
        this.emailId = emailId;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
    }

    public UserInitialDetail(String fbId, String fullName, String emailId, String imageUrl) {
        this.fbId = fbId;
        this.fullName = fullName;
        this.emailId = emailId;
        this.imageUrl = imageUrl;
    }

    protected UserInitialDetail(Parcel in) {
        fbId = in.readString();
        fullName = in.readString();
        emailId = in.readString();
        imageUrl = in.readString();
        phoneNumber = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static final Creator<UserInitialDetail> CREATOR = new Creator<UserInitialDetail>() {
        @Override
        public UserInitialDetail createFromParcel(Parcel in) {
            return new UserInitialDetail(in);
        }

        @Override
        public UserInitialDetail[] newArray(int size) {
            return new UserInitialDetail[size];
        }
    };

    public String getFbId() {
        return fbId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fbId);
        dest.writeString(fullName);
        dest.writeString(emailId);
        dest.writeString(imageUrl);
        dest.writeString(phoneNumber);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
