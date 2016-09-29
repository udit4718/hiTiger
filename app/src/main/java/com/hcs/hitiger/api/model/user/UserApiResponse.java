package com.hcs.hitiger.api.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/6/16.
 */
public class UserApiResponse implements Parcelable {
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("createTime")
    private long createTime;
    @SerializedName("role")
    private String role;
    @SerializedName("status")
    private String status;
    @SerializedName("password")
    private String password;
    @SerializedName("email")
    private String email;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("fbId")
    private String fbId;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("following")
    private boolean following;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("supportUser")
    private boolean supportUser;
    @SerializedName("followersCount")
    private long followersCount;
    @SerializedName("followingCount")
    private long followingCount;
    @SerializedName("gamesPlayed")
    private long gamesPlayed;

    public UserApiResponse(String name, long id, String imageUrl) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    protected UserApiResponse(Parcel in) {
        id = in.readLong();
        name = in.readString();
        createTime = in.readLong();
        role = in.readString();
        status = in.readString();
        password = in.readString();
        email = in.readString();
        mobile = in.readString();
        fbId = in.readString();
        imageUrl = in.readString();
        following = in.readByte() != 0;
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        supportUser = in.readByte() != 0;
        followersCount = in.readLong();
        followingCount = in.readLong();
        gamesPlayed = in.readLong();
    }

    public static final Creator<UserApiResponse> CREATOR = new Creator<UserApiResponse>() {
        @Override
        public UserApiResponse createFromParcel(Parcel in) {
            return new UserApiResponse(in);
        }

        @Override
        public UserApiResponse[] newArray(int size) {
            return new UserApiResponse[size];
        }
    };

    public long getFollowersCount() {
        return followersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFbId() {
        return fbId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isFollowing() {
        return following;
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

    public boolean isSupportUser() {
        return supportUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(createTime);
        dest.writeString(role);
        dest.writeString(status);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(fbId);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (following ? 1 : 0));
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeByte((byte) (supportUser ? 1 : 0));
        dest.writeLong(followersCount);
        dest.writeLong(followingCount);
        dest.writeLong(gamesPlayed);
    }
}
